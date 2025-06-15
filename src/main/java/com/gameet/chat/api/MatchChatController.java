package com.gameet.chat.api;

import java.security.Principal;
import java.util.List;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.ParticipantInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gameet.chat.service.MatchChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MatchChatController {

    private final MatchChatService matchChatService;

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long roomId) {
        List<ChatMessage> messages = matchChatService.getChatMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/participantId/{roomId}")
    public ResponseEntity<ParticipantInfoResponse> getMyParticipantInfo(@PathVariable Long roomId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        ParticipantInfoResponse info = matchChatService.getMyParticipantInfo(roomId, userId);
        return ResponseEntity.ok(info);
    }

    @PatchMapping("/{matchRoomId}/complete")
    public ResponseEntity<Void> completeMatch(@PathVariable Long matchRoomId) {
        matchChatService.completeMatch(matchRoomId);
        return ResponseEntity.ok().build();
    }
}