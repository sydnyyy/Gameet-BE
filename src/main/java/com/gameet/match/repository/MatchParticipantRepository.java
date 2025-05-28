package com.gameet.match.repository;

import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    boolean existsByMatchParticipantIdAndMatchRoom_matchStatus(Long userId, MatchStatus matchStatus);

    @Query("""
        SELECT mp.userProfile.user.userId
        FROM MatchParticipant mp
        WHERE mp.matchRoom.matchRoomId = :matchRoomId
    """)
    List<Long> findUserIdsByMatchRoomId(@Param("matchRoomId") Long matchRoomId);
}
