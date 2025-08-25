package kr.hhplus.be.server.application.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class AsyncQueueService {

    private static final String QUEUE_KEY = "queue:waiting";
    private static final String TOKEN_KEY_PREFIX = "queue:token:";

    private final RedisTemplate<String, String> redisTemplate;
    private final Duration tokenTTL = Duration.ofMinutes(10);

    public AsyncQueueService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 대기열 토큰 발급
     */
    public String issueToken(Long userId) {
        String tokenKey = TOKEN_KEY_PREFIX + userId;
        String existingToken = redisTemplate.opsForValue().get(tokenKey);

        if (existingToken != null) {
            return existingToken; // 유효 토큰 재사용
        }

        String token = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        // Redis에 기록 (RDBMS는 배치에서 동기화)
        redisTemplate.opsForValue().set(tokenKey, token, tokenTTL);
        redisTemplate.opsForZSet().add(QUEUE_KEY, userId.toString(), now);

        return token;
    }

    /**
     * 대기열 순위 조회
     */
    public Long getRank(Long userId) {
        Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, userId.toString());
        return (rank != null) ? rank + 1 : null;
    }

    /**
     * 토큰 만료
     */
    public void expireToken(Long userId) {
        redisTemplate.delete(TOKEN_KEY_PREFIX + userId);
        redisTemplate.opsForZSet().remove(QUEUE_KEY, userId.toString());
    }
}
