package com.example.microsweeper.rest;

import com.example.microsweeper.service.ScoreboardService;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Health
public class ServiceHealth implements HealthCheck {


    @Inject
    private ScoreboardService svc;

    @Override
    public HealthCheckResponse call() {

        try {
            svc.getScoreboard();
            return HealthCheckResponse.named("service-health").up().build();
        } catch (Exception ex) {
            return HealthCheckResponse.named("service-health").withData("reason", "service bad").down().build();
        }

    }
}