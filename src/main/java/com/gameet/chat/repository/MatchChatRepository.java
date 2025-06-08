package com.gameet.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gameet.chat.entity.MatchChat;

@Repository
public interface MatchChatRepository extends JpaRepository<MatchChat, Long> {

    @Query("SELECT c FROM MatchChat c WHERE c.matchParticipant.matchRoom.matchRoomId = :matchRoomId")
    List<MatchChat> findByMatchRoomId(@Param("matchRoomId") Long matchRoomId);
}