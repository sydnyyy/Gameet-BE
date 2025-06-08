package com.gameet.chat.api;

import java.security.Principal;
import java.util.List;

import com.gameet.user.dto.response.UserDetailsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.MatchChatResponse;
import com.gameet.chat.service.MatchChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MatchChatController {

    private final MatchChatService matchChatService;

    @PostMapping
    public ResponseEntity<MatchChatResponse> sendChat(
              @RequestBody ChatMessage dto,
              Principal principal
    ) {
        return ResponseEntity.ok(matchChatService.saveChat(dto, principal));
    }

    @GetMapping("/{matchRoomId}")
    public ResponseEntity<List<MatchChatResponse>> getChats(@PathVariable Long matchRoomId) {
        return ResponseEntity.ok(matchChatService.getChats(matchRoomId));
    }

    @GetMapping("/opponent")
    public ResponseEntity<UserDetailsResponse> getOpponentInfo(
              @RequestParam Long roomId,
              @RequestParam Long myProfileId
    ) {
        UserDetailsResponse opponentInfo = matchChatService.getOpponentInfo(roomId, myProfileId);
        return ResponseEntity.ok(opponentInfo);
    }
}