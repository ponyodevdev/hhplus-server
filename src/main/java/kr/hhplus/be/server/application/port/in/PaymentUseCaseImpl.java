package kr.hhplus.be.server.application.port.in;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.application.port.in.aop.DistributedLock;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.application.service.PaymentDomainService;
import kr.hhplus.be.server.domain.model.Payment;
import kr.hhplus.be.server.domain.model.TransactionType;
import kr.hhplus.be.server.infrastructure.persistence.PaymentJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class PaymentUseCaseImpl implements PaymentUseCase {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PointHistoryPort pointHistoryPort;

    @PersistenceContext
    private EntityManager em;

    public PaymentUseCaseImpl(PaymentJpaRepository paymentJpaRepository, PointHistoryPort pointHistoryPort) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.pointHistoryPort = pointHistoryPort;
    }

    @Override
    @DistributedLock(
            key = "'user:points:' + #userId",
            waitTime = 5,
            leaseTime = -1, // Watchdog 자동 연장
            fair = true     // 순차 보장
    )
    public void payment(long userId, long amount, LocalDateTime now) {
        Payment payment = paymentJpaRepository.findById(userId).orElseThrow();
        payment.usePoint(amount);

        pointHistoryPort.insert(
                userId,
                amount,
                TransactionType.USE,
                now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
        );
    }


}
