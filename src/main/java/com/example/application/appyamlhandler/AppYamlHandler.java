package com.example.application.appyamlhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppYamlHandler {


    @PostConstruct
    public void init(){

        Map<String, Object> yamlMap = AppYamlHandler.readYamlFromRessources("application.yml");
        //Get a Parameter by path
        var dburl = AppYamlHandler.getValueByPath(yamlMap, "spring.datasource.url");

        //Simple add of a parameter
        yamlMap.put("timestamp", LocalDateTime.now().toString());

        //Set a parameter
        AppYamlHandler.setValueByPath(yamlMap, "spring.datasource.url", "test");

        //Write application.yml
        writeMapToYamlinRessources(yamlMap, "application.yml");

    }


    public static Map<String, Object> readYamlFromRessources(String file) {
        InputStream inputStream = AppYamlHandler.class.getClassLoader().getResourceAsStream(file);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            return objectMapper.readValue(inputStream, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static Object getValueByPath(Map<String, Object> map, String path) {
        String[] p = path.split("\\.");
        Map<String, Object> currentMap = map;
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

    private static void setValueByPath(Map<String, Object> map, String path, Object value) {
        String[] p = path.split("\\.");
        Map<String, Object> currentMap = map;
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


    public static void writeMapToYamlinRessources(Map<String, Object> map, String fileName) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            String resourcesPath = AppYamlHandler.class.getResource("/").getPath();
            String filePath = resourcesPath + fileName;
            objectMapper.writeValue(new File(filePath), map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
