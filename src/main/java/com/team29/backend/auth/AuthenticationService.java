package com.team29.backend.auth;
import com.team29.backend.config.JwtService;
import com.team29.backend.exception.UserRegistrationDetailsMissingException;
import com.team29.backend.exception.UsernameTakenException;
import com.team29.backend.model.Role;
import com.team29.backend.model.User;
import com.team29.backend.repository.UserRepository;

import ch.qos.logback.core.util.Duration;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Value("${cookies.domain}")
  private String domain;
 
  public AuthenticationResponse register(RegisterRequest request) {
  
    Optional<User> EmailTaken = repository.findByEmail(request.getEmail());
    if (EmailTaken.isPresent()){
        throw new UsernameTakenException();
    }
    
    if((StringUtils.isBlank(request.getFirstname())) || (StringUtils.isBlank(request.getLastname())) || (StringUtils.isBlank(request.getPassword())) || (StringUtils.isBlank(request.getEmail()))){
        throw new UserRegistrationDetailsMissingException();
    }
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();
    repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();

  }

  
 

  public AuthenticationResponse login(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }





}
