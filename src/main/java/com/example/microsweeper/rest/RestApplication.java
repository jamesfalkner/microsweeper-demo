package com.example.microsweeper.rest;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@Health
@ApplicationPath("/")
public class RestApplication extends Application implements HealthCheck {


    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("successful-check").up().build();
    }
}
