package com.kafkaConsumer.in.service.provider;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProviderFactory<U extends ISimpleProvider> {
    private final ListableBeanFactory beanFactory;

    @Autowired
    public ProviderFactory(final ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * @param clazz
     * @param s
     * @return The matching implementation or the default implementation if none are found.
     */
    public U getImplementation(final Class<U> clazz, final String s) {
        List<U> tList = this.beanFactory.getBeansOfType(clazz).values().stream().filter((t) -> {
            return t.getProviderIdentifier() != null && t.getProviderIdentifier().equals(s);
        }).toList();
        if (tList.size() > 1) {
            throw new RuntimeException("Multiple implementations found");
        } else {
            return tList.size() == 1 ? (U) tList.get(0) : this.getDefaultImplementation(clazz);
        }
    }

    private <U extends ISimpleProvider> U getDefaultImplementation(final Class<U> clazz) {
        List<U> tList = this.beanFactory.getBeansOfType(clazz).values().stream().filter(ISimpleProvider::isDefault).toList();
        if (tList.size() > 1) {
            throw new RuntimeException("Multiple default implementations found");
        } else if (tList.size() == 1) {
            return (U) tList.get(0);
        } else {
            throw new RuntimeException("No implementation found");
        }
    }

    /**
     * @param clazz
     * @param s
     * @return listeners
     */
    public List<U> getImplementations(final Class<U> clazz, final String s) {
        return this.beanFactory.getBeansOfType(clazz).values().stream().filter((t) -> {
            return t.getProviderIdentifier() != null && t.getProviderIdentifier().equals(s);
        }).toList();
    }
}
