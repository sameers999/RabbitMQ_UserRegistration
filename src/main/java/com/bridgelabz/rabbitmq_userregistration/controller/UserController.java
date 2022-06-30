package com.bridgelabz.rabbitmq_userregistration.controller;

import com.bridgelabz.rabbitmq_userregistration.config.MQConfig;
import com.bridgelabz.rabbitmq_userregistration.dto.ResponseDTO;
import com.bridgelabz.rabbitmq_userregistration.dto.UserDTO;
import com.bridgelabz.rabbitmq_userregistration.dto.UserLoginDTO;
import com.bridgelabz.rabbitmq_userregistration.model.User;
import com.bridgelabz.rabbitmq_userregistration.service.IUserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;
    /**
     * Using RabbitMQ to pass messages
     */
    @Autowired
    private RabbitTemplate template;

    /**
     * @param userDTO
     * @return Ability to Create account
     */

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> addUser(@Valid @RequestBody UserDTO userDTO) {
        String newUser = userService.addUser(userDTO);
        ResponseDTO responseDTO = new ResponseDTO("User Registered Successfully", newUser);
        return new ResponseEntity(responseDTO, HttpStatus.CREATED);
    }

    /**
     * @param userDTO
     * @return Ability to login
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        ResponseDTO login = userService.loginUser(userLoginDTO);
        userLoginDTO.setEmail(UUID.randomUUID().toString());
        login.setMessageDate(new Date());
        template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, login);
        return new ResponseEntity<ResponseDTO>(login, HttpStatus.OK);
    }

    //    Ability to getAll user
    @GetMapping(value = "/getAll")
    public ResponseEntity<String> getAllUser() {
        List<User> listOfUsers = userService.getAllUsers();
        ResponseDTO dto = new ResponseDTO("User retrieved successfully (:", listOfUsers);
        return new ResponseEntity(dto, HttpStatus.OK);
    }

    //    Ability to get user by token
    @GetMapping(value = "/getUser/{token}")
    public ResponseEntity<ResponseDTO> getAllUserDataByToken(@PathVariable String token) {
        Object user = this.userService.getUserByToken(token);
        ResponseDTO response = new ResponseDTO("Requested User : ", user);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    //    Ability to Update by id
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateRecordById(@PathVariable Integer id, @Valid @RequestBody UserDTO userDTO) {
        User entity = userService.updateRecordById(id, userDTO);
        ResponseDTO dto = new ResponseDTO("User Record updated successfully", entity);
        userDTO.setCity(UUID.randomUUID().toString());
        dto.setMessageDate(new Date());
        template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, dto);
        return new ResponseEntity(dto, HttpStatus.ACCEPTED);
    }

    //to delete specific user using id provided
    @DeleteMapping({"/delete/{id}"})
    public ResponseEntity<ResponseDTO> deleteById(@PathVariable Integer id) {
        ResponseDTO response = new ResponseDTO("User deleted successfully", userService.deleteById(id));
        return new ResponseEntity(response, HttpStatus.OK);
    }

    //Ability to verify user by token
    @GetMapping("/verify/{token}")
    ResponseEntity<ResponseDTO> verifyUser(@Valid @PathVariable String token) {
        String userVerification = userService.verifyUser(token);
        if (userVerification != null) {
            ResponseDTO responseDTO = new ResponseDTO("User verified :", userVerification);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO("User Not verified data:", userVerification);
            return new ResponseEntity(responseDTO, HttpStatus.OK);
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<ResponseDTO> forgotPassword(@RequestParam String email) {
        Object user = userService.forgotPassword(email);
        ResponseDTO response = new ResponseDTO("Check your email to reset your password!!", user);
        response.setMessageDate(new Date());
        template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestParam String token, @RequestParam String password) {
        String user = userService.resetPassword(token, password);
        ResponseDTO response = new ResponseDTO("Reset Password", user);
        response.setMessageDate(new Date());
        template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //--------------------API Calls for RestTemplate----------------------------//

    @GetMapping("/findById/{userId}")
    ResponseEntity<ResponseDTO> getByIdAPI(@PathVariable Integer userId) {
        User user = userService.getByIdAPI(userId);
        ResponseDTO response = new ResponseDTO("User Id found", user);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * @param userDTO
     * @return Ability to add user and display message to rabbitMQ
     */
    @PostMapping("/registerRabbitMQ")
    public ResponseEntity<ResponseDTO> insertUser(@Valid @RequestBody UserDTO userDTO) {
        String newUser = userService.addUser(userDTO);
        ResponseDTO responseDTO = new ResponseDTO("User Registered Successfully", newUser);
        userDTO.setCity(UUID.randomUUID().toString());
        responseDTO.setMessageDate(new Date());
        template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, responseDTO);
        return new ResponseEntity(responseDTO, HttpStatus.CREATED);
    }
}