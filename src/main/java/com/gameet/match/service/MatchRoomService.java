package com.gameet.match.service;

import com.gameet.match.dto.request.MatchRoomInsert;
import com.gameet.match.entity.MatchRoom;
import com.gameet.match.repository.MatchRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchRoomService {

    private final MatchRoomRepository matchRoomRepository;

    @Transactional
    public MatchRoom createMatchRoom(MatchRoomInsert matchRoomInsert) {
        MatchRoom matchRoom = MatchRoom.of(matchRoomInsert);

        return matchRoomRepository.save(matchRoom);
    }
}
