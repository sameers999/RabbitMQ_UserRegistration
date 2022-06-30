package com.bridgelabz.rabbitmq_userregistration.service;

import com.bridgelabz.rabbitmq_userregistration.dto.ResponseDTO;
import com.bridgelabz.rabbitmq_userregistration.dto.UserDTO;
import com.bridgelabz.rabbitmq_userregistration.dto.UserLoginDTO;
import com.bridgelabz.rabbitmq_userregistration.exception.UserException;
import com.bridgelabz.rabbitmq_userregistration.model.User;
import com.bridgelabz.rabbitmq_userregistration.repository.UserRepository;
import com.bridgelabz.rabbitmq_userregistration.util.EmailSenderService;
import com.bridgelabz.rabbitmq_userregistration.util.TokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IUserService{
    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailSenderService mailService;

    @Autowired
    TokenUtility util;

    @Override
    public String addUser(UserDTO userDTO) {
        User newUser = new User(userDTO);
        Optional<User> userEmail= userRepository.findByEmailid(userDTO.getEmail());
        if(userEmail.isPresent()){
            throw new UserException(HttpStatus.BAD_REQUEST,"Email already exists, Please enter other email!!") ;
        }else
            userRepository.save(newUser);
        String token = util.createToken(newUser.getUserId());
        mailService.sendEmail(newUser.getEmail(), "User Registration", " Hi " + newUser.getFirstName() +
                " Your User Registered SuccessFully Completed. Please Click here to get data-> " +
                "http://localhost:8012/user/verify/" + token);
        return token;
    }

    @Override
    public String verifyUser(String token) {
        int id = util.decodeToken(token);
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user.toString();
        } else return "User not Verified!!";
    }

    @Override
    public ResponseDTO loginUser(UserLoginDTO userLoginDTO) {
        ResponseDTO dto = new ResponseDTO();
        Optional<User> login = userRepository.findByEmailid(userLoginDTO.getEmail());
        if (login.isPresent()) {
            String pass = login.get().getPassword();
            if (login.get().getPassword().equals(userLoginDTO.getPassword())) {
                mailService.sendEmail(login.get().getEmail(), "User Registration", " Hi Welcome back " + login.get().getFirstName() + ". You have Successfully Loggedin. ");
                dto.setMessage("login successful ");
                dto.setData(login.get());
                return dto;
            } else {
                mailService.sendEmail(login.get().getEmail(), "User Registration", " Hi " + login.get().getFirstName() + ". your password is wrong try again later! ");
                dto.setMessage("Sorry! login is unsuccessful");
                dto.setData("Wrong password");

                return dto;
            }
        }
        return new ResponseDTO("User not found!", "Wrong email");
    }


    @Override
    public List<User> getAllUsers() {
        List<User> getUsers = userRepository.findAll();
        if (getUsers.isEmpty()) {
            throw new UserException(HttpStatus.NOT_FOUND, "There is no User added yet");
        } else return getUsers;
    }

    @Override
    public Object getUserByToken(String token) {
        int id = util.decodeToken(token);
        Optional<User> getUser = userRepository.findById(id);
        if (getUser.isPresent()) {
            mailService.sendEmail(getUser.get().getEmail(), "User Registration", "Hi " + getUser.get().getFirstName() + " Please Click here to get data-> " + "http://localhost:8012/user/getAll/");
            return getUser;

        } else {
            throw new UserException(HttpStatus.NOT_FOUND, "Record for provided userId is not found");
        }
    }

    @Override
    public User updateRecordById(Integer id, UserDTO userDTO) {
        Optional<User> updateUser = userRepository.findById(id);
        if (updateUser.isPresent()) {
            User newUser = new User(updateUser.get().getUserId(), userDTO);
            userRepository.save(newUser);
            String token = util.createToken(newUser.getUserId());
            mailService.sendEmail(newUser.getEmail(), "Welcome " + newUser.getFirstName(), "Your User Registration details updated successfully!!");
            return newUser;
        }
        throw new UserException(HttpStatus.NOT_FOUND, "User Details for id not found");
    }


    @Override
    public Object forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmailid(email);
        String Token = userOptional.get().getToken();
        if (!userOptional.isPresent()) {
            return "Invalid email id.";
        } else {
            User user = userOptional.get();
            user.setToken(generateToken());
            user.setTokenCreationDate(LocalDateTime.now());

            user = userRepository.save(user);

            mailService.sendEmail(userOptional.get().getEmail(), "Reset Password", "Hi "
                    + userOptional.get().getFirstName() + "\n" +
                    "You're receiving this email because you requested a password reset \n" + "Click the following link to change the password : " + "http://localhost:8012/user/resetPassword?token=" + user.getToken());
        }

        return Token;
    }

    @Override
    public String resetPassword(String token, String password) {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (!userOptional.isPresent()) {
            return "Invalid token.";
        }

        LocalDateTime tokenCreationDate = userOptional.get().getTokenCreationDate();

        if (isTokenExpired(tokenCreationDate)) {
            return "Token Expired.";
        }
        User user = userOptional.get();

        user.setPassword(password);
        user.setToken(null);
        user.setTokenCreationDate(null);
        mailService.sendEmail(userOptional.get().getEmail(), "Welcome " + userOptional.get().getFirstName(), "Your User Registration Password reset successfully completed \n" + " You login by clicking this link " + " http://localhost:8012/user/login/");
        userRepository.save(user);
        return "Your password successfully updated.";
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
    }

    @Override
    public User getByIdAPI(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserException(HttpStatus.NOT_FOUND, "There are no users with given id");
        }
        return user.get();
    }

    @Override
    public Object deleteById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserException(HttpStatus.NOT_FOUND, "Invalid UserId..please input valid Id");
        }
        userRepository.deleteById(id);
        mailService.sendEmail(user.get().getEmail(), "Hi" + user.get().getFirstName(), "Your User Registration Account deleted successfully!!");
        return user.get();
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();
        return token.append(UUID.randomUUID().toString()).append(UUID.randomUUID().toString()).toString();
    }

}
