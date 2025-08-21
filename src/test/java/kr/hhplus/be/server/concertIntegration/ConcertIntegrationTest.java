package kr.hhplus.be.server.concertIntegration;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import kr.hhplus.be.server.application.port.in.SeatUseCase;
import kr.hhplus.be.server.application.port.in.aop.DistributedLockAop;
import kr.hhplus.be.server.application.port.out.QueuePort;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.application.service.QueueTokenDomainService;
import kr.hhplus.be.server.domain.model.Seat;
import kr.hhplus.be.server.infrastructure.persistence.SeatJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcertIntegrationTest {

    @Autowired
    QueueTokenDomainService queueTokenDomainService;

    @Autowired
    QueuePort queuePort;

    @Autowired
    SeatUseCase seatUseCase;

    @Autowired
    SeatPort seatPort;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    Clock clock = Clock.systemUTC();

    @BeforeEach
    void setup() {
        seatJpaRepository.save(new Seat(2L, 3L, null));
    }

    private void seedSeat(Long seatId, Long optionId, String seatLabel) {
        Seat seat = new Seat(seatId, optionId, seatLabel);
        seatJpaRepository.save(seat);
    }

    // ===== 1. 오버셀 방지 =====
    @Test
    void 동시성환경에서도_오버셀은_발생하지않는다() throws Exception {
        Long seatId = 1L;
        Long optionId = 101L;
        seedSeat(seatId, optionId, "A1");

        UUID[] users = IntStream.range(0, 10)
                .mapToObj(i -> UUID.randomUUID())
                .toArray(UUID[]::new);


        ExecutorService es = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());

        for (UUID user : users) {
            es.submit(() -> {
                try {
                    seatUseCase.assignSeat(seatId, user);
                    results.add(true);
                } catch (Exception e) {
                    results.add(false);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        long success = results.stream().filter(r -> r).count();
        long fail = results.stream().filter(r -> !r).count();

        assertThat(success).isEqualTo(1);
        assertThat(fail).isEqualTo(9);
    }


    // ===== 2. 중복 토큰 방지 =====
    @Test
    void 동일유저는_토큰이_하나만_발급된다() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now(clock);
        Duration ttl = Duration.ofMinutes(5);

        UUID t1 = queueTokenDomainService.issueToken(userId, now, ttl);
        UUID t2 = queueTokenDomainService.issueToken(userId, now, ttl);
        UUID t3 = queueTokenDomainService.issueToken(userId, now, ttl);

        // 항상 같은 토큰만 유지
        assertThat(t1).isEqualTo(t2).isEqualTo(t3);
        assertThat(queuePort.findValidTokenByUserId(userId, now)).isPresent();
    }

    // ===== 3. 멱등성 보장 =====
    @Test
    void 동일좌석_재요청시_최종상태는_일관적이다() {
        Long seatId = 2L;
        UUID userId = UUID.randomUUID();

        boolean first = tryAssign(seatId, userId);
        boolean second = tryAssign(seatId, userId);

        //  첫 시도는 성공, 두 번째 시도는 실패해야 함
        assertThat(first).isTrue();
        assertThat(second).isFalse();

        // DB 최종 상태는 좌석이 점유된 상태
        Seat seat = seatPort.findById(seatId).orElseThrow();
        assertThat(seat.isOccupied(LocalDateTime.now())).isTrue();
    }

    // ===== 4. 락 → 트랜잭션 순서 보장 =====
    @Test
    void 반드시_락을_획득한후_트랜잭션이_실행된다() throws Exception {
        // 1) 로그 캡처 세팅
        Logger logger = (Logger) LoggerFactory.getLogger(DistributedLockAop.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        Long seatId = 3L;
        seedSeat(seatId, 101L, "A1");

        ExecutorService es = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            UUID user = UUID.randomUUID();
            es.submit(() -> {
                try {
                    seatUseCase.assignSeat(seatId, user);
                } catch (Exception ignore) {
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // 2) 로그 메시지 모으기
        List<String> logs = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        // 3) 순서 검증
        int lockIdx = logs.indexOf("LOCK ACQUIRED: seat:" + seatId);
        int txIdx   = logs.indexOf("TX EXECUTED: seat:" + seatId);

        assertThat(lockIdx).isNotEqualTo(-1);
        assertThat(txIdx).isNotEqualTo(-1);
        assertThat(lockIdx).isLessThan(txIdx);

        // 4) DB 결과 검증
        Seat seat = seatPort.findById(seatId).orElseThrow();
        assertThat(seat.getOwnerId()).isNotNull();
    }

    private boolean tryAssign(Long seatId, UUID userId) {
        try {
            seatUseCase.assignSeat(seatId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
