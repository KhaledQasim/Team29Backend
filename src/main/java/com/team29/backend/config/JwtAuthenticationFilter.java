package com.team29.backend.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.apache.coyote.Response;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Io;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;



@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,  @NonNull HttpServletResponse response,   @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;
            if (request.getCookies() == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Optional<Cookie> jwtOpt = Arrays.stream(request.getCookies())
                    .filter(cookie -> "jwt".equals(cookie.getName()))
                    .findAny();

            if (jwtOpt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { //if we have the userEmail and they are not authenticated
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); //then we get the user details from the database
                if (jwtService.isTokenValid(jwt, userDetails)) { //checks if the user is valid or not , if the user is valid it will create an object of type Username and password authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities()); //userDetails is null as at this point the user does not have stored credentials in our dataBase
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //extends the created authToken with the details of our request
                    SecurityContextHolder.getContext().setAuthentication(authToken); //updates the authentication token
                }
            }
            filterChain.doFilter(request, response);
        }

        catch (ServletException Se) {
             new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Servlet Exception in JWT Authentication Filter", Se);
        }
        catch (IOException e) {
             new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "IO Exception in JWT Authentication Filter", e);
        }
    }
     
      
}
