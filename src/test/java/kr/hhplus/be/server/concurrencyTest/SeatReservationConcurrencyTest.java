package kr.hhplus.be.server.concurrencyTest;

import kr.hhplus.be.server.application.port.in.ReservationUseCaseImpl;
import kr.hhplus.be.server.application.port.out.ReservationPort;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeatReservationConcurrencyTest {

    @Autowired
    private ReservationUseCaseImpl reservationUseCase;

    @Autowired
    private SeatPort seatPort;

    @Autowired
    private ReservationPort reservationPort;

    private Long testSeatId;

    @BeforeEach
    void setUp() {
        // 테스트 전에 좌석 초기화
        testSeatId = 100L;
        Seat seat = new Seat(testSeatId, 200L, "A1");
        seatPort.save(seat); // 혹은 insert용 test용 repository 메서드
    }

    @Test
    @DisplayName("여러 사용자가 동시에 같은 좌석을 예약할 때 단 한 명만 성공해야 한다")
    void concurrentSeatReservationTest() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        LocalDateTime now = LocalDateTime.now();

        List<UUID> userIds = new ArrayList<>();
        IntStream.range(0, numberOfThreads).forEach(i -> userIds.add(UUID.randomUUID()));

        List<Future<Boolean>> results = new ArrayList<>();

        for (UUID userId : userIds) {
            Future<Boolean> future = executor.submit(() -> {
                latch.await(); // 동시에 시작
                try {
                    reservationUseCase.reserveSeat(testSeatId, userId, now);
                    return true; // 성공
                } catch (IllegalStateException e) {
                    return false; // 실패
                }
            });
            results.add(future);
        }

        // 모든 스레드 시작
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        long successCount = results.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .filter(success -> success)
                .count();

        assertThat(successCount).isEqualTo(1L);
    }
}
