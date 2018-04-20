package com.serverless;

import java.util.Map;

public class Response {

	private final String message;
	private final Map<String, Object> input;
	private final Throwable e;

	public Response(String message, Map<String, Object> input) {
		this.message = message;
		this.input = input;
		this.e = null;
	}
	
	public Response(String message, Map<String, Object> input, Throwable e) {
        this.message = message;
        this.input = input;
        this.e = e;
    }

	public String getMessage() {
		return this.message;
	}

	public Map<String, Object> getInput() {
		return this.input;
	}
	
	public Throwable getError() {
        return this.e;
    }
}
