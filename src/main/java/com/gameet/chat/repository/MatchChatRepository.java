package com.gameet.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gameet.chat.entity.MatchChat;

import java.util.List;

@Repository
public interface MatchChatRepository extends JpaRepository<MatchChat, Long> {
    @Query("SELECT c FROM MatchChat c WHERE c.matchParticipant.matchRoom.matchRoomId = :matchRoomId ORDER BY c.sendAt ASC")
    List<MatchChat> findByMatchRoomId(@Param("matchRoomId") Long matchRoomId);
}