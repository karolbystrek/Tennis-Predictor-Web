package com.karolbystrek.tennispredictor.exceptions;

public class PredictionServiceException extends RuntimeException {
    private final Integer statusCode;

    public PredictionServiceException(String message) {
        super(message);
        this.statusCode = null;
    }

    public PredictionServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
