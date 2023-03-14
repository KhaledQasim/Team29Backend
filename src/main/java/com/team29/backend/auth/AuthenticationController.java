package com.team29.backend.auth;

import com.team29.backend.exception.UserRegistrationDetailsMissingException;
import com.team29.backend.exception.UserRegistrationDetailsMissingException;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.team29.backend.config.JwtService;
import com.team29.backend.exception.UserEmailWrongException;
import com.team29.backend.exception.UserRegistrationDetailsMissingAdvice;
import com.team29.backend.exception.UsernameTakenException;
import com.team29.backend.exception.WrongPassE;
import com.team29.backend.ip.RequestService;
import com.team29.backend.model.Role;
import com.team29.backend.model.User;

import ch.qos.logback.core.util.Duration;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.team29.backend.repository.UserRepository;
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080" }, allowCredentials = "true")
public class AuthenticationController {
    private final UserRepository repository;
    private final AuthenticationService service;
    private final JwtService jwt;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Value("${cookies.domain}")
    private String domain;

    @Autowired
    private RequestService requestService;


    // @PostMapping("/register")
    // public ResponseEntity<AuthenticationResponse> register(
    //     @RequestBody RegisterRequest request
    // ){
    //     return ResponseEntity.ok(service.register(request));
    // }
    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
    @ResponseBody
    @PostMapping("/register")
    // HttpServletRequest requestIp,
    public ResponseEntity<?> register( HttpServletRequest requestIp, @RequestBody RegisterRequest request) {
        Optional<User> EmailTaken = repository.findByEmail(request.getEmail());
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
        try {
          
            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .ip(requestService.getClientIp(requestIp))
                    .build();
            repository.save(user);
            // String token = jwt.generateToken(user)
            // return ResponseEntity.ok()
            // .header(
            //         HttpHeaders.AUTHORIZATION,
            //         jwt.generateToken(user)
            // )
            // .body(token);
            String token = jwt.generateToken(user);
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .domain(domain)
                    .path("/")
                    .maxAge(Duration.buildByDays(365).getMilliseconds())
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(token);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@CookieValue(name = "jwt") String token,
            @AuthenticationPrincipal User user) {
        try {
            Boolean isValidToken = jwt.isTokenValid(token, user);
            return ResponseEntity.ok(isValidToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .domain(domain)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).body("ok");
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            var user = repository.findByEmail(request.getEmail())
                    .orElseThrow();

            String token = jwt.generateToken(user);
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .domain(domain)
                    .path("/")
                    .maxAge(Duration.buildByDays(365).getMilliseconds())
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(token);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
