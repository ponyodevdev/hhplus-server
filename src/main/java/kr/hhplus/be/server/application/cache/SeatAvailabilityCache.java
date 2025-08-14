package kr.hhplus.be.server.application.cache;

import kr.hhplus.be.server.application.port.out.SeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SeatAvailabilityCache {

    private final StringRedisTemplate redis;
    private final SeatPort seatPort;

    private String key(Long optionId) { return "seat:avail:" + optionId; }

    // 캐시 조회, 미스면 DB로 초기화
    public long getOrBuild(Long optionId, LocalDateTime now) {
        String v = redis.opsForValue().get(key(optionId));
        if (v != null) return Long.parseLong(v);

        long cnt = seatPort.countAvailableByOption(optionId, now);
        redis.opsForValue().set(key(optionId), Long.toString(cnt));
        return cnt;
    }

    // 예약/확정 후 커밋 시 -1
    public void decrementAfterCommit(Long optionId) {
        afterCommit(() -> redis.opsForValue().decrement(key(optionId)));
    }
    // 취소/만료 확정 후 커밋 시 +1
    public void incrementAfterCommit(Long optionId) {
        afterCommit(() -> redis.opsForValue().increment(key(optionId)));
    }

    private void afterCommit(Runnable r) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { r.run(); }
            });
        } else {
            r.run();
        }
    }
}

