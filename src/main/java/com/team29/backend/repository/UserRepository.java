package com.team29.backend.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team29.backend.model.User;
//Repository is used to connect to the DB using an ID
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    
}
