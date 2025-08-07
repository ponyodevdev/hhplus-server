package kr.hhplus.be.server.application.port.out;

import kr.hhplus.be.server.domain.model.Payment;

public interface PaymentPort {
    boolean existsById(long userId);
    Payment findById(long userId);
    long findPointByUserId(long userId);
    void insertOrUpdate(long userId, long point);
    Payment selectById(long userId);
    void save(Payment payment);
}
