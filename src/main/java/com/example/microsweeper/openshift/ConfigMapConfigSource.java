package com.example.microsweeper.openshift;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1ConfigMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class ConfigMapConfigSource implements ConfigSource {
    
    private Map<String, String> config = null;

    public static final String NS_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/namespace";
    public static final String CONFIGMAP_NAME = "microsweeper";
    @Override
    public int getOrdinal() {
        return 112;
    }    

    @Override
    public Set<String> getPropertyNames() {
        fetch();
        return config.keySet();
    }

    @Override
    public Map<String, String> getProperties() {
        fetch();
        return config;
    }

    @Override
    public String getValue(String key) {
        fetch();
        return config.get(key);
    }

    @Override
    public String getName() {
        return "ConfigMapConfigSource";
    }

    private void fetch() {
        
        if (config != null) { 
            return;
        }

        config = new HashMap<>();

        try {
            ApiClient client = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api(client);

            final String ns =
                new String(Files.readAllBytes(Paths.get(NS_PATH)), Charset.defaultCharset());

            V1ConfigMap map = api.readNamespacedConfigMap(CONFIGMAP_NAME, ns, null, null, null);

            Map<String, String> rawConfig = map.getData();
            rawConfig.keySet().forEach(key -> {
                config.put(key, rawConfig.get(key));
            });

            
        } catch (Exception e) {
            System.err.println("Exception when calling CoreV1Api#readNamespacedConfigMap");
            e.printStackTrace();
        }
        
    }
}
