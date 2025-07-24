package kr.hhplus.be.server.application.port.in;

import java.time.LocalDateTime;

public interface ChargePointUseCase {
    void charge(long userId, long amount, LocalDateTime now);
}
