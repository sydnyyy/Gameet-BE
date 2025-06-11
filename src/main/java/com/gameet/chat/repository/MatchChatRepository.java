package com.gameet.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gameet.chat.entity.MatchChat;

@Repository
public interface MatchChatRepository extends JpaRepository<MatchChat, Long> {
}