package kr.hhplus.be.server.redisIntergration;

import kr.hhplus.be.server.RedisLockTestBase;
import kr.hhplus.be.server.application.port.in.QueueTokenUseCase;
import kr.hhplus.be.server.application.port.out.QueuePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import kr.hhplus.be.server.domain.model.Queue;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class QueueTokenConcurrencyIntegrationTest extends RedisLockTestBase {

    @Autowired QueueTokenUseCase queueTokenUseCase;
    @Autowired QueuePort queuePort;

    @Test
    void 같은유저_동시_토큰발급_하나로_수렴() throws Exception {
        UUID userId = UUID.randomUUID();

        int threads = 30;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<UUID>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(es.submit(() -> {
                ready.countDown();
                start.await();
                return queueTokenUseCase.issueToken(userId);
            }));
        }

        ready.await();
        start.countDown();

        Set<UUID> tokens = new HashSet<>();
        for (Future<UUID> f : futures) tokens.add(f.get());
        es.shutdown();

        // 모두 같은 토큰이어야 함
        org.assertj.core.api.Assertions.assertThat(tokens.size()).isEqualTo(1);

        // 실제 저장된 유효 토큰도 1개 존재하고, 그 토큰과 일치
        LocalDateTime now = LocalDateTime.now();
        UUID saved = queuePort.findValidTokenByUserId(userId, now)
                .map(Queue::getTokenId)
                .orElseThrow();
        org.assertj.core.api.Assertions.assertThat(tokens.iterator().next()).isEqualTo(saved);
    }
}
