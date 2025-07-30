package kr.hhplus.be.server.application.port.in;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public interface QueueTokenUseCase {
    UUID issueToken(UUID userId);
    Duration getRemainingTime(UUID userId, LocalDateTime now);
}
