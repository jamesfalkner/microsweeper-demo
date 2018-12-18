package com.example.microsweeper.rest;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@ApplicationScoped
@Path("/config")
public class ConfigEndpoint {

    @Inject
    @ConfigProperty(name = "microsweeper.bg", defaultValue = "blue")
    private String bg;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<String, String>() 
        { 
            private static final long serialVersionUID = 1L;

            {
                put("bg", bg);
            } 
        }; 
        
        return config;
    }
}
