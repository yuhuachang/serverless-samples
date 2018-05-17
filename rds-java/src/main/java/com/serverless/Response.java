package com.serverless;

import java.util.List;
import java.util.Map;

public class Response {

    private final String message;
    private final Map<String, Object> input;
    private final List<Map<String, Object>> data;

    public Response(String message, Map<String, Object> input, List<Map<String, Object>> data) {
        this.message = message;
        this.input = input;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getInput() {
        return input;
    }
    
    public List<Map<String, Object>> getData() {
        return data;
    }
}
