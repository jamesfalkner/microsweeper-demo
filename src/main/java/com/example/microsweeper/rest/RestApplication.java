package com.example.microsweeper.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@ApplicationPath("/api")
public class RestApplication extends Application implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("basic-check").up().build();
	}

}
