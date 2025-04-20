package com.karolbystrek.tennispredictor.exceptions;

public class PredictionServiceException extends RuntimeException {
    private final int statusCode;

    public PredictionServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
