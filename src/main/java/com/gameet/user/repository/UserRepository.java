package com.gameet.user.repository;

import com.gameet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String Email);
    Optional<User> findByEmail(String Email);
}
