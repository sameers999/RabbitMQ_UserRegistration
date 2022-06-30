package com.bridgelabz.rabbitmq_userregistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitMqUserRegistrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqUserRegistrationApplication.class, args);
        System.out.println("Welcome to UserRegistration !!");
    }

}
