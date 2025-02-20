package com.ekoservice.orders_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object){
        try{
            return objectMapper.writeValueAsString(object);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz){
        try {
            System.out.println("Deserializing JSON: " + json);
            T result = objectMapper.readValue(json, clazz);
            System.out.println("Deserialized object: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Failed to deserialize JSON: " + json);
            log.error("Error: ",e);
            throw new RuntimeException(e);
        }
    }
}
