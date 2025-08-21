package kr.hhplus.be.server.application.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ConcertRankingService {
    private static final String RANKING_KEY = "concert:ranking";

    private final RedisTemplate<String, String> redisTemplate;

    public ConcertRankingService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 판매량 증가 + 매진 처리 (무조건 SortedSet)
     */
    public void recordSale(Long concertId, boolean soldOut) {
        String concertKey = "concert" + concertId;

        if (soldOut) {
            // 매진 → 점수를 Double.MAX_VALUE 로 설정
            redisTemplate.opsForZSet().add(RANKING_KEY, concertKey, Double.MAX_VALUE);
        } else {
            // 판매량 증가
            redisTemplate.opsForZSet().incrementScore(RANKING_KEY, concertKey, 1);
        }
    }

    /**
     * Top-N 조회 (매진은 무조건 맨 앞)
     */
    public List<String> getRanking(int topN) {
        Set<String> ranking = redisTemplate.opsForZSet()
                .reverseRange(RANKING_KEY, 0, topN - 1);

        return ranking == null ? List.of() : new ArrayList<>(ranking);
    }
}

