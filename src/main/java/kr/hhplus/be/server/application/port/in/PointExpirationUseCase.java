package kr.hhplus.be.server.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

public interface PointExpirationUseCase {
    void expirePointsForUsers(List<Long> userIds, LocalDateTime now);
}
