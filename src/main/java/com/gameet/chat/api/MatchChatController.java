package com.gameet.chat.api;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.MatchChatResponse;
import com.gameet.chat.dto.OpponentProfileResponse;
import com.gameet.chat.service.MatchChatService;
import com.gameet.user.dto.response.UserDetailsResponse;

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

    @GetMapping("/opponent/{roomId}/{myProfileId}")
    public ResponseEntity<OpponentProfileResponse> getOpponentProfile(
        @PathVariable Long roomId,
        @PathVariable Long myProfileId
    ) {
        OpponentProfileResponse response = matchChatService.getOpponentProfile(roomId, myProfileId);
        return ResponseEntity.ok(response);
    }
}