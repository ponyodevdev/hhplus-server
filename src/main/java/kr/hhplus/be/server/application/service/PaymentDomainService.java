package kr.hhplus.be.server.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.domain.model.Payment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentDomainService {

    private final PaymentPort paymentPort;

    @PersistenceContext
    private EntityManager em;

    public PaymentDomainService(PaymentPort paymentPort) {
        this.paymentPort = paymentPort;
    }

    public void validateUserExists(long userId) {
        if (!paymentPort.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
    }

    public long getCurrentPoint(long userId) {
        return paymentPort.findPointByUserId(userId);
    }

    @Transactional
    public void use(long userId, long amount) {
        Payment payment = paymentPort.findById(userId); // 엔티티 조회 (version 포함)
        payment.usePoint(amount); // 도메인 메서드로 차감 (내부에서 검증)

        // 엔티티 자체를 저장해야 @Version이 동작함
        paymentPort.save(payment); // JPA의 save() 또는 Spring Data JPA 사용
        em.flush(); // 낙관적 락 즉시 트리거
    }

}
