package kr.hhplus.be.server.concurrencyTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.application.port.in.PaymentUseCase;
import kr.hhplus.be.server.domain.model.Payment;

import kr.hhplus.be.server.infrastructure.persistence.PaymentJpaRepository;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointUsageConcurrencyTest {

    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    @PersistenceContext
    private EntityManager em;

    private final long userId = 999L;

    @BeforeEach
    void setup() {
        paymentJpaRepository.deleteAll();
        Payment payment = new Payment(userId, 1000L);
        paymentJpaRepository.save(payment);
        paymentJpaRepository.flush(); // 꼭 추가
    }

    @Test
    @DisplayName("동시 포인트 사용 요청 시 낙관적 락으로 일부 실패")
    void concurrentPointUsage_shouldCauseOptimisticLockingFailure() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<Boolean>> futures = new ArrayList<>();

        long amountToUse = 500L;

        for (int i = 0; i < threadCount; i++) {
            Future<Boolean> future = executor.submit(() -> {
                try {
                    paymentUseCase.payment(userId, amountToUse, LocalDateTime.now());
                    return true;
                } catch (OptimisticLockException | StaleObjectStateException e) {
                    System.out.println(Thread.currentThread().getName() + " 낙관적 락 실패");
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    latch.countDown();
                }
            });

            futures.add(future);
        }

        latch.await();
        executor.shutdown();

        // 성공 개수 계산
        long successCount = 0;
        for (Future<Boolean> future : futures) {
            try {
                if (future.get()) successCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("성공한 스레드 수: " + successCount);
        assertThat(successCount).isLessThan(threadCount);


        Payment updated = paymentJpaRepository.findById(userId).orElseThrow();

        assertThat(successCount).isLessThan(threadCount);
        assertThat(updated.getCurrentPoint()).isEqualTo(0L);
        assertThat(updated.getUsedPoint()).isEqualTo(1000L);
    }
}
