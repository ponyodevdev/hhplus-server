package kr.hhplus.be.server.redisIntergration;

import kr.hhplus.be.server.RedisLockTestBase;
import kr.hhplus.be.server.application.port.in.PaymentUseCase;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PointUsageConcurrencyIntegrationTest extends RedisLockTestBase {

    @Autowired PaymentUseCase paymentUseCase;
    @Autowired PointHistoryPort historyPort;

    @Test
    void 같은유저_동시_결제_총차감이_초기잔액을_넘지않는다() throws Exception {
        long userId = 1L;
        seedBalance(userId, 1000); // 초기 1000 충전(CHARGE)

        int threads = 20;
        long each = 100;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            results.add(es.submit(() -> {
                ready.countDown();
                start.await();
                try {
                    paymentUseCase.payment(userId, each, LocalDateTime.now());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }));
        }

        ready.await();
        start.countDown();

        int success = 0;
        for (Future<Boolean> f : results) if (f.get()) success++;
        es.shutdown();

        // 초기 1000에서 100씩 → 최대 10건만 성공 가능
        org.assertj.core.api.Assertions.assertThat(success).isLessThanOrEqualTo(10);

        long used = totalUsed(userId);
        long balance = currentBalance(userId);

        org.assertj.core.api.Assertions.assertThat(used).isLessThanOrEqualTo(1000L);
        org.assertj.core.api.Assertions.assertThat(balance).isBetween(0L, 1000L);
    }

    private void seedBalance(long userId, long amount) {
        historyPort.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());
    }

    private long currentBalance(long userId) {
        return historyPort.selectAllByUserId(userId).stream()
                .mapToLong(h -> (h.getType() == TransactionType.CHARGE ? h.getAmount() :
                        h.getType() == TransactionType.USE    ? -h.getAmount() : 0))
                .sum();
    }

    private long totalUsed(long userId) {
        return historyPort.selectAllByUserId(userId).stream()
                .filter(h -> h.getType() == TransactionType.USE)
                .mapToLong(PointHistory::getAmount)
                .sum();
    }
}

