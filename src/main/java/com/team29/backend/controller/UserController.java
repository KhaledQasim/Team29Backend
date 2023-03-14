package com.team29.backend.controller;


import com.team29.backend.repository.UserRepository;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.team29.backend.ip.RequestService;
import com.team29.backend.auth.RegisterRequest;
import com.team29.backend.exception.UserEmailWrongException;
import com.team29.backend.exception.UserNotFoundE;
import com.team29.backend.exception.UserRegistrationDetailsMissingException;
import com.team29.backend.exception.UsernameTakenException;
import com.team29.backend.exception.WrongPassE;
import com.team29.backend.model.Role;
import com.team29.backend.model.User;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
public class UserController {
    private final PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private RequestService requestService;
    private Role role;


    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    @GetMapping("/get")
    List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
  
    @PostMapping("/post")
    public void register( HttpServletRequest requestIp, @RequestBody RegisterRequest request) {
        Optional<User> EmailTaken = userRepository.findByEmail(request.getEmail());
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if ((StringUtils.isBlank(request.getFirstname())) || (StringUtils.isBlank(request.getLastname()))
                || (StringUtils.isBlank(request.getPassword())) || (StringUtils.isBlank(request.getEmail()))) {

            throw new UserRegistrationDetailsMissingException();
        }

        if (EmailTaken.isPresent()) {
            throw new UsernameTakenException();
        }

        if (request.getPassword().length() <= 7) {
            throw new WrongPassE();
        }
        if (!patternMatches(request.getEmail(), regexPattern)) {
            throw new UserEmailWrongException();
        }
       
            if(request.getRole().equals("ADMIN")){
                role = Role.ADMIN;
            }
            else {
                role = Role.USER;
            }
            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .ip(requestService.getClientIp(requestIp))
                    .build();
            userRepository.save(user);
            // String token = jwt.generateToken(user)
            // return ResponseEntity.ok()
            // .header(
            //         HttpHeaders.AUTHORIZATION,
            //         jwt.generateToken(user)
            // )
            // .body(token);
           
     
    }

    @PutMapping("/put/{id}")
    User updateUser(@RequestBody User newUser, @PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstname(newUser.getFirstname());
                    user.setLastname(newUser.getLastname());
                    user.setRole(newUser.getRole());
                    return userRepository.save(user);
                }).orElseThrow(() -> new UserNotFoundE(id));
    }

    @GetMapping("/get/{id}")
    User getUserByID(@PathVariable Long id){
        return userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundE(id));
    }

    
    @DeleteMapping("/delete/{id}")
    String deleteUser(@PathVariable Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundE(id);
        }
        userRepository.deleteById(id);
        return "User with id: "+id+" has been deleted!";
    }
    
  
}
