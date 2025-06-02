package com.gameet.match.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameet.match.domain.MatchCondition;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MatchRepository {

    private static final String MATCH_PREFIX = "match_user:";
    private static final String MATCH_CONDITION_PREFIX = "match_condition:";
    private static final String MATCH_LOCK_PREFIX = "match_lock:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final ThreadLocal<Map<Long, String>> lockTokenMap = ThreadLocal.withInitial(HashMap::new);

    @PostConstruct
    public void init() {
        redisTemplate.delete(MATCH_PREFIX);
        Set<String> keys = redisTemplate.keys(MATCH_CONDITION_PREFIX + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void addMatchUser(Long userId) {
        redisTemplate.opsForZSet().add(MATCH_PREFIX, userId.toString(), System.currentTimeMillis());
    }

    public void removeMatchUser(Long userId) {
        redisTemplate.opsForZSet().remove(MATCH_PREFIX, userId.toString());
    }

    public Set<Long> getAllMatchUsers() {
        Set<String> matchUsers = redisTemplate.opsForZSet().range(MATCH_PREFIX, 0, -1);
        if (matchUsers != null) {
            return matchUsers.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        }
        return null;
    }

    public boolean isMatchUserExists(Long userId) {
        Double score = redisTemplate.opsForZSet().score(MATCH_PREFIX, userId.toString());
        return score != null;
    }

    public Long getElapsedTime(Long userId) {
        Double score = redisTemplate.opsForZSet().score(MATCH_PREFIX, userId.toString());
        if (score != null) {
            long currentTime = System.currentTimeMillis();
            return (currentTime - score.longValue()) / 1000;
        }

        return 0L;
    }

    public Boolean tryLock(Long userId) {
        String token = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(getMatchLockKey(userId), token, Duration.ofSeconds(10));
        if (Boolean.TRUE.equals(success)) {
            lockTokenMap.get().put(userId, token);
        }
        return success;
    }

    public void releaseLock(Long userId) {
        String key = getMatchLockKey(userId);
        String token = lockTokenMap.get().get(userId);
        if (token == null) return;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else return 0 end"
        );
        redisScript.setResultType(Long.class);

        redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                token
        );

        lockTokenMap.get().remove(userId);
    }

    public void saveMatchCondition(Long userId, Map<String, String> matchCondition) {
        redisTemplate.opsForHash().putAll(getMatchConditionKey(userId), matchCondition);
    }

    public void removeMatchCondition(Long userId) {
        redisTemplate.delete(getMatchConditionKey(userId));
    }

    public MatchCondition getMatchConditionByUserId(Long userId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(getMatchConditionKey(userId));

        Map<String, Object> normalizedEntries = new HashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String val = String.valueOf(entry.getValue()).trim();
            Object parsed = val.isEmpty() ? null : parseIfJsonArray(val);
            normalizedEntries.put(key, parsed);
        }

        return objectMapper.convertValue(normalizedEntries, MatchCondition.class);
    }

    private Object parseIfJsonArray(String value) {
        if (value != null && value.startsWith("[") && value.endsWith("]")) {
            try {
                return objectMapper.readValue(value, List.class);
            } catch (JsonProcessingException e) {
                return value;
            }
        }
        return value;
    }

    private String getMatchConditionKey(Long userId) {
        return MATCH_CONDITION_PREFIX + userId + ":";
    }

    private String getMatchLockKey(Long userId) {
        return MATCH_LOCK_PREFIX + userId + ":";
    }
}
