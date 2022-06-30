package com.bridgelabz.rabbitmq_userregistration.dto;

import lombok.Data;

import java.util.Date;

public @Data class ResponseDTO {
    private String message;
    private Object data;
    private Date messageDate;

    public ResponseDTO(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(String message, Object data, Date messageDate) {
        this.message = message;
        this.data = data;
        this.messageDate = messageDate;
    }

    public ResponseDTO() {
    }
}
