package com.example.utility;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class HTTPRequestParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static RequestBody parseRequestBody(InputStream inputStream) throws IOException {
        return objectMapper.readValue(inputStream, RequestBody.class);
    }
}