package com.bridgelabz.rabbitmq_userregistration.service;

import com.bridgelabz.rabbitmq_userregistration.dto.ResponseDTO;
import com.bridgelabz.rabbitmq_userregistration.dto.UserDTO;
import com.bridgelabz.rabbitmq_userregistration.dto.UserLoginDTO;
import com.bridgelabz.rabbitmq_userregistration.model.User;

import java.util.List;

public interface IUserService {
    String addUser(UserDTO userDTO);

    String verifyUser(String token);

    ResponseDTO loginUser(UserLoginDTO userLoginDTO);

    List<User> getAllUsers();

    Object getUserByToken(String token);

    User updateRecordById(Integer id, UserDTO userDTO);

    Object forgotPassword(String email);

    String resetPassword(String token, String password);

    User getByIdAPI(Integer userId);

    Object deleteById(Integer id);
}
