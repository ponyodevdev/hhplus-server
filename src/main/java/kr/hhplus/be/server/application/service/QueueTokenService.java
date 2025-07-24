package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.QueueTokenUseCase;
import kr.hhplus.be.server.application.port.out.QueuePort;
import kr.hhplus.be.server.domain.model.Queue;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

public class QueueTokenService implements QueueTokenUseCase {

    private final QueuePort queuePort;
    private final Duration tokenTTL;
    private final Clock clock;

    public QueueTokenService(QueuePort queuePort, Duration tokenTTL, Clock clock) {
        this.queuePort = queuePort;
        this.tokenTTL = tokenTTL;
        this.clock = clock;
    }

    @Override
    public UUID issueToken(UUID userId) {
        LocalDateTime now = LocalDateTime.now(clock);
        Queue token = new Queue(UUID.randomUUID(), userId, now, tokenTTL);
        queuePort.save(token);
        return token.getTokenId();
    }

    @Override
    public Duration getRemainingTime(UUID tokenId, LocalDateTime now) {
        Queue token = queuePort.findById(tokenId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 대기열 토큰입니다."));
        if (token.isExpired(now)) {
            throw new IllegalStateException("대기열 토큰이 만료되었습니다.");
        }
        return token.remainingWaitTime(now);
    }

    // 테스트 전용 접근자
    public Queue getTokenStatus(UUID tokenId, LocalDateTime now) {
        Queue token = queuePort.findById(tokenId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 대기열 토큰입니다."));
        if (token.isExpired(now)) {
            throw new IllegalStateException("대기열 토큰이 만료되었습니다.");
        }
        return token;
    }
}
