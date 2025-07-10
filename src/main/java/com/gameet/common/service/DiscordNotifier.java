package com.gameet.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DiscordNotifier {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${discord.webhook.websocket-uri}")
    private String websocketWebHookUrl;

    public void send(String title, String description) {
        Map<String, Object> embed = new HashMap<>();
        embed.put("title", title);
        embed.put("description", description);
        embed.put("color", 0xFF0000);

        Map<String, Object> payload = new HashMap<>();
        payload.put("embeds", List.of(embed));

        try {
            restTemplate.postForObject(websocketWebHookUrl, payload, String.class);
        } catch (Exception e) {
            log.error("Discord 알림 전송 실패. 메시지: '{}', 오류: {}", description, e.getMessage(), e);
        }
    }
}
