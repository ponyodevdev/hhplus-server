package kr.hhplus.be.server.application.port.in;

import java.time.LocalDateTime;

public interface PaymentUseCase {
    void payment(long userId, long amount, LocalDateTime now);
}
