package com.gameet.user.repository;

import com.gameet.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    boolean existsByNickname(String nickname);

    @Query("select u from UserProfile u where u.user.userId = :userId")
    Optional<UserProfile> findByUserId(@Param("userId") Long userId);
}
