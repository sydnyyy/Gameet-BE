package com.gameet.chat.api;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.MatchParticipantsInfoResponse;
import com.gameet.chat.service.MatchChatService;
import com.gameet.global.dto.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{matchRoomId}/participantsInfo")
    public ResponseEntity<MatchParticipantsInfoResponse> getParticipants(@PathVariable Long matchRoomId,
                                                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MatchParticipantsInfoResponse matchParticipantsInfo = matchChatService.getMatchParticipantsInfo(matchRoomId, userPrincipal.getUserId());
        return ResponseEntity.ok(matchParticipantsInfo);
    }

    @PatchMapping("/{matchRoomId}/complete")
    public ResponseEntity<Void> completeMatch(@PathVariable Long matchRoomId) {
        matchChatService.completeMatch(matchRoomId);
        return ResponseEntity.ok().build();
    }
}