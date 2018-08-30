package com.example.microsweeper.service;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

public class EnvironmentAlternativesExtension implements Extension {

    private String environment = System.getenv("ENVIRONMENT");
    private EnvironmentType currentEnvironment = (environment != null) ?
            EnvironmentType.valueOf(environment) : EnvironmentType.DEVELOPMENT;

    public <T> void processAnotated(@Observes ProcessAnnotatedType<T> event) {
        EnvironmentAlternative alternative =
                event.getAnnotatedType().getJavaClass()
                        .getAnnotation(EnvironmentAlternative.class);
        if (alternative != null && !containsCurrentEnvironment(alternative.value())) {
            event.veto();
        }
    }

    private boolean containsCurrentEnvironment(EnvironmentType[] environments) {


        for (EnvironmentType environment : environments) {
            if (environment == currentEnvironment) {
                return true;
            }
        }
        return false;
    }
}
