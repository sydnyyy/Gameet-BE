package com.gameet.chat.api;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gameet.chat.service.MatchChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MatchChatController {

    private final MatchChatService matchChatService;

    @GetMapping("/participantId/{roomId}")
    public ResponseEntity<Long> getMyParticipantId(@PathVariable Long roomId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Long participantId = matchChatService.getMyParticipantId(roomId, userId);
        return ResponseEntity.ok(participantId);
    }

    @PatchMapping("/{matchRoomId}/complete")
    public ResponseEntity<Void> completeMatch(@PathVariable Long matchRoomId) {
        matchChatService.completeMatch(matchRoomId);
        return ResponseEntity.ok().build();
    }
}