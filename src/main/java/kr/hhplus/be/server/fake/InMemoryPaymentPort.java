package kr.hhplus.be.server.fake;

import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.domain.model.Payment;

import java.util.HashMap;
import java.util.Map;

public class InMemoryPaymentPort implements PaymentPort {
    private final Map<Long, Payment> storage = new HashMap<>();

    @Override
    public boolean existsById(long userId) {
        return storage.containsKey(userId);
    }

    @Override
    public Payment findById(long userId) {
        return storage.get(userId);
    }

    @Override
    public long findPointByUserId(long userId) {
        Payment payment = storage.get(userId);
        if (payment == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return payment.point(); // Payment 도메인에 point() 메서드가 있어야 함
    }

    @Override
    public Payment selectById(long userId) {
        return storage.get(userId);
    }

    @Override
    public void insertOrUpdate(long userId, long point) {
        storage.put(userId, new Payment(userId, point)); // Payment 생성자에 (userId, point) 있어야 함
    }
}

