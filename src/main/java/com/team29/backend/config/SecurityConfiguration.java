package com.team29.backend.config;


import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;
    

    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                    .csrf()
                    .disable()
                    .cors()
                    .disable()
                    .authorizeHttpRequests()

                    .requestMatchers( "/auth/**","/product**/**","/api/**","/api/calendar/**",,"/carts**/**").permitAll() //any urls in this list will not need authentication token

                    //.requestMatchers(HttpMethod.POST, "/auth/**","/product**/**").permitAll()
                    //.requestMatchers("/products").hasAuthority("ADMIN") //any urls in here can be only reached with a ADMIN role account.
                    // .requestMatchers("/product**").hasRole("ADMIN")                                          
                    .anyRequest()
                    .authenticated()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// tells spring to create a new session for each request,  this is needed so that each request is authenticated and no user session data is stored on the server side.
                    .and()
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        

        return httpSecurity.build();
    }


   

    
   
    
}
