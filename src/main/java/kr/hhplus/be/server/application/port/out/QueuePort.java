package kr.hhplus.be.server.application.port.out;

import kr.hhplus.be.server.domain.model.Queue;

import java.util.Optional;
import java.util.UUID;

public interface QueuePort {
    void save(Queue token);
    Optional<Queue> findById(UUID tokenId);
}
