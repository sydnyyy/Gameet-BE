package com.gameet.chat.api;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.MatchParticipantsInfoResponse;
import com.gameet.chat.service.MatchChatService;
import com.gameet.global.annotation.AccessLoggable;
import com.gameet.global.dto.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "매칭 참가자 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 참가자 정보 조회", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "매칭 참여자가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @AccessLoggable(action = "매칭 참가자 정보 조회")
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

    @PatchMapping("/read/{matchParticipantId}")
    public ResponseEntity<Void> updateLastReadAt(@PathVariable Long matchParticipantId) {
        matchChatService.updateLastReadAt(matchParticipantId);
        return ResponseEntity.ok().build();
    }
}