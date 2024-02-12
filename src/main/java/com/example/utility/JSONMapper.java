package com.example.utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String objectToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error mapping object to JSON string", e);
        }
    }

    public static <T> T stringToObject(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error mapping JSON string to object", e);
        }
    }
}