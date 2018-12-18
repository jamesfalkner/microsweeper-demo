package com.example.microsweeper.rest;

import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;
import org.eclipse.microprofile.opentracing.Traced;


import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

@ApplicationScoped
public class ExternalService {

    @Traced(operationName = "call-external-httpbin")
    public void callExternal() {

            for (int i = 0; i < 3; i++) {
                Client client = ClientTracingRegistrar.configure(ClientBuilder.newBuilder()).build();
                try {
                    int delay = (int)Math.floor(1 + (Math.random() * 3));
                    WebTarget target = client.target("http://httpbin.org/delay/" + delay);
                    target.request().get();            
                } finally {
                client.close();
            }

    }

    }
}
