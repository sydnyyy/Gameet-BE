package com.gameet.chat.entity;

import java.time.LocalDateTime;

import com.gameet.chat.enums.MessageType;
import com.gameet.match.entity.MatchParticipant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "match_chat")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchChatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_participant_id", nullable = false)
    private MatchParticipant matchParticipant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime sendAt;
}