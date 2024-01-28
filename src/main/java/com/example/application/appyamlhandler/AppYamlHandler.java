package com.example.application.appyamlhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppYamlHandler {

    private Map<String, Object> yamlMap;

    @PostConstruct
    public void init() {
        yamlMap = readYamlFromRessources("application.yml");
        //Get a Parameter by path
        //var dburl = getValueByPath("spring.datasource.url");

        //Simple add of a parameter
        yamlMap.put("timestamp", LocalDateTime.now().toString());

        //Set a parameter
        //setValueByPath("spring.datasource.url", "test");

        //Write application.yml
        //writeMapToYamlinRessources("application.yml");
    }


    private Map<String, Object> readYamlFromRessources(String file) {
        InputStream inputStream = AppYamlHandler.class.getClassLoader().getResourceAsStream(file);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            return objectMapper.readValue(inputStream, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Object getValueByPath(String path) {
        String[] p = path.split("\\.");
        Map<String, Object> currentMap = yamlMap;
        for (String key : p) {
            if (currentMap.containsKey(key)) {
                Object obj = currentMap.get(key);
                if (obj instanceof Map) {
                    currentMap = (Map<String, Object>) obj;
                } else {
                    return obj;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    public void setValueByPath(String path, Object value) {
        String[] p = path.split("\\.");
        Map<String, Object> currentMap = yamlMap;
        for (int i = 0; i < p.length; i++) {
            String key = p[i];
            if (i == p.length - 1) {
                // Reached the end of the path, set the value
                currentMap.put(key, value);
            } else {
                // Traverse to the next level in the map
                currentMap.computeIfAbsent(key, k -> new HashMap<>());
                currentMap = (Map<String, Object>) currentMap.get(key);
            }
        }
    }

    public void writeMapToYamlinRessources() {
        String fileName = "application.yml";
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            String resourcesPath = AppYamlHandler.class.getResource("/").getPath();
            String filePath = resourcesPath + fileName;
            objectMapper.writeValue(new File(filePath), yamlMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}