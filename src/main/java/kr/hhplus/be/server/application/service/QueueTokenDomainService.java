package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.QueuePort;
import kr.hhplus.be.server.domain.model.Queue;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Component
public class QueueTokenDomainService {

    private final QueuePort queuePort;

    public QueueTokenDomainService(QueuePort queuePort) {
        this.queuePort = queuePort;
    }

    public void saveToken(Queue token) {
        queuePort.save(token);
    }

    public UUID issueToken(UUID userId, LocalDateTime now, Duration tokenTTL) {
        // Step 1. 기존 유효 토큰 있는지 확인
        Optional<Queue> existingToken = queuePort.findValidTokenByUserId(userId, now);

        // Step 2. 이미 유효한 토큰이 있으면 그거 반환
        if (existingToken.isPresent()) {
            return existingToken.get().getTokenId();
        }

        // Step 3. 없으면 새로 발급
        Queue token = new Queue(UUID.randomUUID(), userId, now, tokenTTL);
        queuePort.save(token);
        return token.getTokenId();
    }

    public Duration getRemainingTime(UUID tokenId, LocalDateTime now) {
        Queue token = queuePort.findById(tokenId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 대기열 토큰입니다."));
        if (token.isExpired(now)) {
            throw new IllegalStateException("대기열 토큰이 만료되었습니다.");
        }
        return token.remainingWaitTime(now);
    }

    // 선택: 상태 반환용 (관리자 용도 등)
    public Queue getTokenStatus(UUID tokenId, LocalDateTime now) {
        Queue token = queuePort.findById(tokenId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 대기열 토큰입니다."));
        if (token.isExpired(now)) {
            throw new IllegalStateException("대기열 토큰이 만료되었습니다.");
        }
        return token;
    }
}
