package com.example.application.appyamlhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class AppYamlHandler {


    @PostConstruct
    public void init(){

        Map<String, Object> yamlMap = AppYamlHandler.readYamlFromRessources("application.yml");
        System.out.println(yamlMap);
        yamlMap.put("timestamp", LocalDateTime.now().toString());

        //Auskommentiert, da sonst nach jedme Start der Anwendung die application.yml geschrieben und dadurch die Anwendung wieder neu gestartet wird
        //writeMapToYamlinRessources(yamlMap, "application.yml");

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

    public static void writeMapToYamlinRessources(Map<String, Object> map, String fileName) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            String resourcesPath = AppYamlHandler.class.getResource("/").getPath();
            String filePath = resourcesPath + fileName;
            objectMapper.writeValue(new File(filePath), map);
            System.out.println("Map has been written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
