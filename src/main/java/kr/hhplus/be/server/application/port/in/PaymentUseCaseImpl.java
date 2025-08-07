package kr.hhplus.be.server.application.port.in;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
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
    @Transactional(rollbackFor = OptimisticLockException.class)
    public void payment(long userId, long amount, LocalDateTime now) {
        Payment payment = paymentJpaRepository.findById(userId).orElseThrow();
        System.out.println(Thread.currentThread().getName() + " 시작 version = " + payment.getVersion());

        // 여기서 사용 (version 필드 변경 유도)
        payment.usePoint(amount);

        // version 변경 커밋을 미루기 위해 delay
        try {
            Thread.sleep(500); // 커밋 타이밍을 늦춰서 다른 스레드가 먼저 커밋하도록 유도
        } catch (InterruptedException ignored) {
        }

        // pointHistory는 트랜잭션 안에서 함께 저장됨
        pointHistoryPort.insert(
                userId,
                amount,
                TransactionType.USE,
                now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
        );
    }


}
