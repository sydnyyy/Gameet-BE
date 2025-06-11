package com.gameet.match.repository;

import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    boolean existsByUserProfile_userProfileIdAndMatchRoom_matchStatus(Long userId, MatchStatus matchStatus);

    @Query("""
        SELECT mp.userProfile.user.userId
        FROM MatchParticipant mp
        WHERE mp.matchRoom.matchRoomId = :matchRoomId
    """)
    List<Long> findUserIdsByMatchRoomId(@Param("matchRoomId") Long matchRoomId);

    @Query("""
        SELECT mp.matchRoom.matchRoomId
        FROM MatchParticipant mp
        WHERE mp.userProfile.userProfileId = :userProfileId
          AND mp.matchRoom.matchStatus = 'MATCHED'
    """)
    Long findMatchRoomIdByUserProfileId(@Param("userProfileId") Long userProfileId);

    @Query("""
        SELECT mp
        FROM MatchParticipant mp
        JOIN FETCH mp.userProfile up
        WHERE mp.matchRoom.matchRoomId = :roomId
          AND up.userProfileId <> :myProfileId
    """)
    Optional<MatchParticipant> findOpponentProfileByRoomIdAndExcludeMyProfile(
              @Param("roomId") Long roomId,
              @Param("myProfileId") Long myProfileId
    );

    @Query("""
        SELECT mp
        FROM MatchParticipant mp
        WHERE mp.matchRoom.matchRoomId = :roomId
          AND mp.userProfile.user.userId = :userId
    """)
    Optional<MatchParticipant> findByMatchRoomIdAndUserId(
              @Param("roomId") Long roomId,
              @Param("userId") Long userId
    );
}
