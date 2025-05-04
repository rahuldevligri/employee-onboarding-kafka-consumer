package com.kafkaConsumer.in.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kafkaConsumer.in.dto.EmployeeOnboardPayload;
import com.kafkaConsumer.in.dto.KafkaEvent;
import com.kafkaConsumer.in.dto.ProfileStatus;
import com.kafkaConsumer.in.dto.Role;
import com.kafkaConsumer.in.persistence.entity.*;
import com.kafkaConsumer.in.persistence.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
@Slf4j
public class EmployeeOnboardService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientEntityRepository clientEntityRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserEntityMappingRepository userEntityMappingRepository;
    @Autowired
    private UserPlatformMappingRepository userPlatformMappingRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @param kafkaEvent
     */
    public void onboard(final KafkaEvent kafkaEvent) {
        EmployeeOnboardPayload payload = parseKafkaEvent(kafkaEvent);
        log.info("Received Kafka event for onboarding: {}", kafkaEvent);
        if (payload == null) {
            log.warn("Skipping onboarding due to missing mandatory fields.");
            return;
        }
        UUID userId = UUID.fromString(payload.getUserId());
        User user = setUserData(userId, payload);

        UUID clientId = UUID.fromString(kafkaEvent.getClientId());
        UUID clientEntityId = saveOrUpdateClientEntity(payload, clientId);
        saveUser(user, clientEntityId, payload);
    }

    /**
     * Finds or creates a User and sets data from EmployeeOnboardPayload.
     * @param userId
     * @param payload
     * @return User object with updated details.
     */
    private User setUserData(final UUID userId, final EmployeeOnboardPayload payload) {
        User user = userRepository.findByIdOrAdminUserId(userId).orElseGet(() -> {
            log.info("User not found. Creating new user with ID: {}", userId);
            User newUser = new User();
            newUser.setId(userId);
            return newUser;
        });

        user.setFirstName(payload.getFirstName());
        user.setMiddleName(payload.getMiddleName());
        user.setLastName(payload.getLastName());
        user.setGender(payload.getGender());
        user.setPhone(payload.getPhoneNumber());
        user.setEmail(payload.getEmail());
        if (payload.getAdminUserId() != null) {
            user.setAdminUserId(UUID.fromString(payload.getAdminUserId()));
        } else {
            user.setAdminUserId(null);
        }
        user.setEnabled("ACTIVE".equals(payload.getStatus()));
        log.info("User data set for ID: {} -> {}", userId, user);
        return user;
    }

    private UUID saveOrUpdateClientEntity(final EmployeeOnboardPayload payload, final UUID clientId) {
        Optional<ClientEntity> existingEntity = clientEntityRepository.findById(clientId);
        ClientEntity clientEntity = existingEntity.orElseGet(() -> {
            log.info("ClientEntity not found. Creating new one with ID: {}", clientId);
            ClientEntity newClient = new ClientEntity();
            newClient.setId(clientId);
            newClient.setTimeZone("Asia/Kolkata");
            return newClient;
        });
        clientEntity.setName(payload.getClientName());
        clientEntity.setSeries(payload.getClientSeries());
        UUID savedClientId = clientEntityRepository.save(clientEntity).getId();
        log.info("Successfully saved ClientEntity with ID: {}", savedClientId);
        return savedClientId;
    }

    private void saveUser(final User user, final UUID clientEntityId, final EmployeeOnboardPayload payload) {
        try {
            User savedUser = userRepository.save(user);
            log.info("Successfully saved or updated user: {}", savedUser.getEmail());
            saveProfile(savedUser, clientEntityId, payload);
            mapUserToEntity(savedUser, clientEntityId, payload.getRole());
            mapUserToPlatform(savedUser, payload.getRole(), payload.getPlatform());
        } catch (Exception e) {
            log.error("Error saving user data for {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }

    private void saveProfile(final User user, final UUID clientEntityId, final EmployeeOnboardPayload payload) {
        try {
            Optional<Profile> existingProfile = profileRepository.findByUserIdAndEntityId(user.getId(), clientEntityId);
            Profile profile = existingProfile.orElseGet(() -> {
                log.info("No profile found. Creating new profile for user: {}", user.getEmail());
                Profile newProfile = new Profile();
                newProfile.setUserId(user.getId());
                newProfile.setEntityId(clientEntityId);
                return newProfile;
            });
            String status = payload.getStatus();
            ProfileStatus profileStatus = ProfileStatus.IN_ACTIVE;
            if (status != null && status.equalsIgnoreCase("ACTIVE")) {
                profileStatus = ProfileStatus.ACTIVE;
            }
            profile.setStatus(profileStatus);
            profile.setOtherEmails(List.of((payload.getOtherEmails())));
            try {
                profile.setDateOfBirth(Date.valueOf(payload.getDateOfBirth()));
            } catch (Exception e) {
                log.info("DOB format not correct: {}", payload.getDateOfBirth());
            }
            profile.setEmpCode(payload.getEmployeeCode());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode additionalFieldsNode = objectMapper.valueToTree(payload.getAdditionalFields());

            List<JsonNode> costCenters = getCostCenters(additionalFieldsNode);
            profile.setCostCenters(costCenters);
            log.info("Extracted Cost Centers for {}: {}", user.getEmail(), costCenters);

            ObjectNode updatedMetadata = mergeMetadata(profile.getMetadata(), additionalFieldsNode, user.getEmail());
            if (payload.getEmployeeGrade() != null) {
                updatedMetadata.put("employeeGrade", payload.getEmployeeGrade());
            }
            profile.setMetadata(updatedMetadata);
            profile.setReportingManager(payload.getReportingManager());
            profileRepository.save(profile);
            log.info("{} profile for user: {}", existingProfile.isPresent() ? "Updated" : "Created", user.getEmail());
        } catch (Exception e) {
            log.error("Error saving/updating profile for user {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }

    private List<JsonNode> getCostCenters(final JsonNode additionalFields) {
        if (additionalFields != null && additionalFields.has("Cost Centers")) {
            JsonNode costCentersNode = additionalFields.get("Cost Centers");

            if (costCentersNode.isArray()) {
                // Convert JSON array to a List<JsonNode>
                List<JsonNode> costCentersList = new ArrayList<>();
                costCentersNode.forEach(costCentersList::add);
                log.info("Extracted Cost Centers: {}", costCentersList);
                return costCentersList;
            } else if (costCentersNode.isTextual()) {
                log.info("Single cost center found: {}", costCentersNode);
                return Collections.singletonList(costCentersNode);
            }
        }
        log.info("Cost Centers not found or invalid in additionalFields");
        return Collections.emptyList();
    }

    private ObjectNode mergeMetadata(final JsonNode existingMetadataNode, final JsonNode additionalFieldsNode, final String userEmail) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode mergedMetadata = (existingMetadataNode != null && existingMetadataNode.isObject())
                ? (ObjectNode) existingMetadataNode
                : objectMapper.createObjectNode();

        additionalFieldsNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            if (!key.equalsIgnoreCase("Cost Centers")) {
                JsonNode newValue = entry.getValue();
                if (mergedMetadata.has(key)) {
                    JsonNode oldValue = mergedMetadata.get(key);
                    if (!oldValue.equals(newValue)) {
                        mergedMetadata.set(key, newValue); // update if value changed
                        log.info("Updated metadata key '{}' for user: {}", key, userEmail);
                    } else {
                        log.info("Metadata key '{}' already exists with same value. Skipping update for user: {}", key, userEmail);
                    }
                } else {
                    mergedMetadata.set(key, newValue); // add new key
                    log.info("Added new metadata key '{}' for user: {}", key, userEmail);
                }
            }
        });
        return mergedMetadata;
    }

    private void mapUserToEntity(final User user, final UUID clientEntityId, final String role) {
        try {
            Optional<UserEntityMapping> existingMapping = userEntityMappingRepository
                    .findByUserIdAndEntityId(user.getId(), clientEntityId);

            UserEntityMapping mapping = existingMapping.orElseGet(() -> {
                log.info("Creating new mapping for user {} to entity {}", user.getEmail(), clientEntityId);
                UserEntityMapping newMapping = new UserEntityMapping();
                newMapping.setUserId(user.getId());
                newMapping.setEntityId(clientEntityId);
                newMapping.setRole(getRole(role));
                newMapping.setIsActive(user.getEnabled());
                return newMapping;
            });
            if (existingMapping.isPresent()) {
                mapping.setRole(getRole(role));
                mapping.setIsActive(user.getEnabled());
                log.info("Updated mapping for user {} to entity {}", user.getEmail(), clientEntityId);
            }
            userEntityMappingRepository.save(mapping);
            log.info("Successfully {} mapping for user {} with entity {}",
                    existingMapping.isPresent() ? "updated" : "created",
                    user.getEmail(), clientEntityId);
        } catch (Exception e) {
            log.error("Error mapping user {} to entity {}: {}", user.getEmail(), clientEntityId, e.getMessage(), e);
        }
    }
    private Role getRole(final String role) {
        return (role != null && role.equalsIgnoreCase("ADMIN")) ? Role.ADMIN : Role.INDIVIDUAL;
    }

    private void mapUserToPlatform(final User user, final String role, final String platformName) {
        try {
            log.info("Mapping user {} to platform {}", user.getEmail(), platformName);
            // Check if the platform exists, or create a new one if not found
            Platform platform = platformRepository.findByName(platformName).orElseGet(() -> {
                        log.info("Platform {} not found. Creating new one.", platformName);
                        Platform newPlatform = new Platform();
                        newPlatform.setName(platformName);
                        return platformRepository.save(newPlatform);
                    });
            boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
            Optional<UserPlatformMapping> existingMapping = userPlatformMappingRepository.findByUserIdAndPlatformId(user.getId(), platform.getId());
            if (existingMapping.isEmpty()) {
                // Create new userPlatformMapping if not exists
                UserPlatformMapping newMapping = UserPlatformMapping.builder()
                        .userId(user.getId())
                        .platformId(platform.getId())
                        .isAdmin(isAdmin)
                        .build();
                userPlatformMappingRepository.save(newMapping);
                log.info("Successfully mapped user {} to platform {} as {}.", user.getEmail(), platformName, isAdmin ? "ADMIN" : "USER"); //
            } else {
                // Update existing userPlatformMapping
                UserPlatformMapping mappingToUpdate = existingMapping.get();
                mappingToUpdate.setIsAdmin(isAdmin);
                userPlatformMappingRepository.save(mappingToUpdate);
                log.info("Successfully updated mapping for user {} to platform {} as {}.", user.getEmail(), platformName, isAdmin ? "ADMIN" : "USER");
            }
        } catch (Exception e) {
            log.error("Error mapping user {} to platform {}: {}", user.getEmail(), platformName, e.getMessage(), e);
        }
    }

    private EmployeeOnboardPayload parseKafkaEvent(final KafkaEvent kafkaEvent) {
        JsonNode node = kafkaEvent.getData();
        try {
            if (node.get("userId").isNull() || kafkaEvent.getClientId().isEmpty() || node.get("clientSeries").isNull()
                    || node.get("employeeCode").isNull() || node.get("email").isNull()) {
                throw new IllegalArgumentException("Mandatory fields are missing. Skipping record.");
            }
            return objectMapper.treeToValue(node, EmployeeOnboardPayload.class);
        } catch (JsonProcessingException e) {
            log.error("Error mapping JSON to Payload: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.info("Invalid Kafka event: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error parsing Kafka event: {}", e.getMessage());
            return null;
        }
    }
}
