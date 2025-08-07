package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.application.port.out.QueuePort;
import kr.hhplus.be.server.application.service.QueueTokenDomainService;
import kr.hhplus.be.server.domain.model.Queue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class QueueTokenUseCaseImpl implements QueueTokenUseCase {

    private final QueueTokenDomainService domainService;
    private final Duration tokenTTL;
    private final Clock clock;
    private final QueuePort queuePort;

    public QueueTokenUseCaseImpl(QueueTokenDomainService domainService, Duration tokenTTL, Clock clock, QueuePort queuePort) {
        this.domainService = domainService;
        this.tokenTTL = tokenTTL;
        this.clock = clock;
        this.queuePort = queuePort;
    }

    /*@Override
    public UUID issueToken(UUID userId) {
        return domainService.issueToken(userId, LocalDateTime.now(clock), tokenTTL);
    }*/
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public UUID issueToken(UUID userId) {
        LocalDateTime now = LocalDateTime.now(clock);

        // 1. 락을 걸고 유효 토큰 존재 여부 확인
        Optional<Queue> existing = queuePort.findByUserIdWithLock(userId);

        if (existing.isPresent() && existing.get().getExpiresAt().isAfter(now)) {
            // 유효한 토큰이 이미 존재하면 그걸 재사용
            return existing.get().getTokenId();
        }

        // 없으면 새로 발급
        UUID tokenId = domainService.issueToken(userId, now, tokenTTL);
        Queue queue = new Queue(tokenId, userId, now, now.plus(tokenTTL));

        queuePort.save(queue);
        return tokenId;
    }

    @Override
    public Duration getRemainingTime(UUID tokenId, LocalDateTime now) {
        return domainService.getRemainingTime(tokenId, now);
    }

    // 테스트용 getter
    public Queue getTokenStatus(UUID tokenId, LocalDateTime now) {
        return domainService.getTokenStatus(tokenId, now);
    }
}
