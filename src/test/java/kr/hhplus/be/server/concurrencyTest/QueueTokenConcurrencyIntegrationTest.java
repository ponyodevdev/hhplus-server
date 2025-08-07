package kr.hhplus.be.server.concurrencyTest;

import kr.hhplus.be.server.application.port.in.QueueTokenUseCase;
import kr.hhplus.be.server.application.port.out.QueuePort;
import kr.hhplus.be.server.domain.model.Queue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QueueTokenConcurrencyIntegrationTest {

    @Autowired
    private QueueTokenUseCase queueTokenUseCase;

    @Autowired
    private QueuePort queuePort;

    @Test
    @DisplayName("동일 유저의 중복 토큰 발급을 방지해야 한다 - 동시성 테스트")
    void issueToken_concurrentDuplicateRequestShouldNotCreateMultipleTokens() throws InterruptedException {
        UUID userId = UUID.randomUUID();
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Set<UUID> tokenSet = ConcurrentHashMap.newKeySet(); // 중복 방지 검사용
        List<Future<UUID>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    return queueTokenUseCase.issueToken(userId);
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();
        executorService.shutdown();

        for (Future<UUID> future : futures) {
            try {
                UUID token = future.get();
                tokenSet.add(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // then: 발급된 토큰은 단 하나여야 함
        assertThat(tokenSet).hasSize(1);

        UUID issuedToken = tokenSet.iterator().next();
        Optional<Queue> savedQueue = queuePort.findById(issuedToken);
        assertThat(savedQueue).isPresent();
        assertThat(savedQueue.get().getUserId()).isEqualTo(userId);
    }
}
