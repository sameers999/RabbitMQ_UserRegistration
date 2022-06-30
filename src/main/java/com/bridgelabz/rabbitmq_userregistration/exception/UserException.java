package com.bridgelabz.rabbitmq_userregistration.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

public @Data class UserException extends RuntimeException {
    private String message;
    private HttpStatus httpStatus;

    public UserException(HttpStatus httpStatus, String message) {
        super();
        this.httpStatus = httpStatus;
        this.message = message;
    }
    public UserException(String message) {
        super(message);
    }
}
