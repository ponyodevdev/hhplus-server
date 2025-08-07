package kr.hhplus.be.server.infrastructure.adapter.out;

import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.domain.model.Payment;
import kr.hhplus.be.server.infrastructure.persistence.PaymentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;

@Repository
public class PaymentAdapter implements PaymentPort {

    private final PaymentJpaRepository paymentJpaRepository;


    public PaymentAdapter(PaymentJpaRepository paymentJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    public boolean existsById(long userId) {
        return paymentJpaRepository.existsById(userId);
    }

    @Override
    public Payment findById(long userId) {
        return paymentJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Override
    public long findPointByUserId(long userId) {
        return paymentJpaRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."))
                .point();
    }

    @Override
    public void insertOrUpdate(long userId, long point) {
        Payment payment = paymentJpaRepository.findById(userId)
                .orElse(new Payment(userId, 0L));

        payment.updatePoint(point);
        paymentJpaRepository.save(payment);
    }

    @Override
    public Payment selectById(long userId) {
        return paymentJpaRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 결제 정보가 없습니다."));
    }

    @Override
    public void save(Payment payment) {
        paymentJpaRepository.save(payment); // 여기서 @Version 동작함
    }
}
