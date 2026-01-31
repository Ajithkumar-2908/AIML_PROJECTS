package com.ajithkumar.ragchatmanagement.dto;


import java.time.LocalDateTime;

/**
 * DTO for error responses.
 */

public class ErrorResponse {

    public ErrorResponse(int status, String message, String error, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.timestamp = timestamp;
    }

    private int status;

    private String message;

    private String error;

    private LocalDateTime timestamp;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


}
