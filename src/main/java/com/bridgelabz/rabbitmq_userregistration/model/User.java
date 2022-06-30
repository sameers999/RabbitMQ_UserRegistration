package com.bridgelabz.rabbitmq_userregistration.model;

import com.bridgelabz.rabbitmq_userregistration.dto.UserDTO;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "user_details")
public @Data class User {
    @Id
    @GeneratedValue
    private Integer userId;
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String city;
    private LocalDateTime tokenCreationDate;
    private String token;

    public User() {
    }

    public User(UserDTO userDTO) {
        this.email = userDTO.getEmail();
        this.password = userDTO.getPassword();
        this.confirmPassword = userDTO.getConfirmPassword();
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
        this.dateOfBirth = userDTO.getDateOfBirth();
        this.city = userDTO.getCity();
    }

    public User(Integer userId, UserDTO userDTO) {
        this.userId = userId;
        this.email = userDTO.getEmail();
        this.password = userDTO.getPassword();
        this.confirmPassword = userDTO.getConfirmPassword();
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
        this.dateOfBirth = userDTO.getDateOfBirth();
        this.city = userDTO.getCity();
    }

    public User(String id, UserDTO userDTO) {
        this.email = id;
        this.email = userDTO.getEmail();
        this.password = userDTO.getPassword();
        this.confirmPassword = userDTO.getConfirmPassword();
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
        this.dateOfBirth = userDTO.getDateOfBirth();
        this.city = userDTO.getCity();
    }

    public void UpdateUser(UserDTO userDTO) {
        this.email = userDTO.getEmail();
        this.password = userDTO.getPassword();
        this.confirmPassword = userDTO.getConfirmPassword();
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
        this.dateOfBirth = userDTO.getDateOfBirth();
        this.city = userDTO.getCity();
    }
}
