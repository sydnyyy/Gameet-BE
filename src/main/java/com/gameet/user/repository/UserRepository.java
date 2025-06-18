package com.gameet.user.repository;

import com.gameet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String Email);
    Optional<User> findByEmail(String Email);

    @Query("SELECT u.email FROM User u WHERE u.userId = :userId")
    Optional<String> findEmailByUserId(@Param("userId") Long userId);
}
