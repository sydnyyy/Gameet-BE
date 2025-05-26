package com.gameet.match.repository;

import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    boolean existsByMatchParticipantIdAndMatchRoom_matchStatus(Long userId, MatchStatus matchStatus);
}
