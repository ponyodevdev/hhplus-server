package kr.hhplus.be.server.infrastructure.adapter.out;

import kr.hhplus.be.server.application.port.out.QueuePort;
import kr.hhplus.be.server.domain.model.Queue;
import kr.hhplus.be.server.infrastructure.persistence.QueueJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class QueueAdapter implements QueuePort {

    private final QueueJpaRepository queueJpaRepository;

    public QueueAdapter(QueueJpaRepository queueJpaRepository) {
        this.queueJpaRepository = queueJpaRepository;
    }

    @Override
    public void save(Queue token) {
        queueJpaRepository.save(token);
    }

    @Override
    public Optional<Queue> findById(UUID tokenId) {
        return queueJpaRepository.findById(tokenId);
    }

    @Override
    public Optional<Queue> findValidTokenByUserId(UUID userId, LocalDateTime now) {
        return queueJpaRepository.findValidTokenByUserId(userId, now);
    }

    @Override
    public Optional<Queue> findByUserIdWithLock(UUID userId) {
        return queueJpaRepository.findByUserIdWithLock(userId);
    }
}