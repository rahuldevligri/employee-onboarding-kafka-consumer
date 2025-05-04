# Employee Onboarding Kafka Consumer

A Spring Boot-based Kafka consumer service that listens for `employee-created` events, processes onboarding data, and persists user, profile, and platform mappings into a database.

## 📌 Overview

This service consumes Kafka events containing employee onboarding details, validates the payload, and performs the following actions:
- Creates/updates user records.
- Maps users to client entities and platforms (e.g., Android).
- Manages profiles, metadata, and roles (Admin/Individual).
- Handles additional fields like cost centers and reporting managers.

## ✨ Features

- **Kafka Integration**: Listens to the `employee-created` topic for onboarding events.
- **Data Validation**: Checks for mandatory fields (`userId`, `clientId`, `email`, etc.).
- **Database Operations**: Uses JPA repositories to persist/update users, profiles, and mappings.
- **Dynamic Metadata Handling**: Merges additional fields into user profiles.
- **Platform & Role Mapping**: Maps users to platforms (e.g., Android) with admin/individual roles.

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Apache Kafka**
- **Spring Data JPA**
- **PostgreSQL** (or any JPA-compliant database)
- **Lombok**
- **Jackson** (JSON parsing)

## 📋 Prerequisites

- Java JDK 17+
- Apache Kafka
- Maven/Gradle
- PostgreSQL (or configure another database in `application.properties`)

## 🚀 Getting Started

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/employee-onboarding-kafka-consumer.git
  
2. Build the Project:
  ```bash
  mvn clean install
  ```

3. Configure Kafka and Database:

- Update `application.properties` with your Kafka broker and database credentials:
  ```bash
  properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/your-db
  spring.datasource.username=your-user
  spring.datasource.password=your-password
  
  kafka-config.bootstrap-servers=localhost:9092
  kafka-config.topics.employee-created.name=employee-created
  kafka-config.topics.employee-created.group=onboarding-group
  ```

📤 Payload Structure
- Example Kafka event payload:

  ```bash
  {
    "app": "Android",
    "client_id": "f7439144-c2b9-4661-bad3-97dadfa499ca",
    "metadata": {
      "domain": "employee-created"
    },
    "data": {
      "userId": "90c5f1e4-9d67-4fe7-8c5d-ffb118f2b7a8",
      "gender": "M",
      "email": "rahuldevligri@gmail.com",
      "adminUserId": "3cc2c841-dd4f-4552-b22b-b82ab8619ec6",
      "phoneNumber": "8290813304",
      "firstName": "Rahul",
      "middleName": "Dev",
      "lastName": "Ligri",
      "clientName": "Linkedin",
      "clientSeries": "0058",
      "platform": "Android",
      "employeeCode": "001",
      "dateOfBirth": "1998-04-13",  // Must be in yyyy-MM-dd format
      "status": "active",
      "otherEmails": ["rahuldl@gmail.com"],
      "role": "Admin",
      "additionalFields": {
        "surname": "rahuldevligri"
      }
    }
  }


## 🧩 Key Components
  - **1. Consumer:**
    - EmployeeOnboardConsumer listens to Kafka and routes events to listeners.
  
  - **2. Service:**
    - EmployeeOnboardService handles user onboarding logic, including database operations.
  
  - **3. Event Listener:**
    - EmployeeOnboardEventListener triggers the onboarding process for employee-created events.
  
  - **4. Data Models:**
    - KafkaEvent: Represents the Kafka message structure.
    - EmployeeOnboardPayload: Defines the onboarding data schema.

## 🤝 Contributing
- Fork the repository.

- Create a feature branch: git checkout -b feature/new-feature.

- Commit changes: git commit -m "Add new feature".

- Push to the branch: git push origin feature/new-feature.

- Open a Pull Request.
