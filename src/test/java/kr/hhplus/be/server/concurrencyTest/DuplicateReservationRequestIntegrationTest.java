package kr.hhplus.be.server.concurrencyTest;

import kr.hhplus.be.server.application.port.in.ReservationUseCaseImpl;
import kr.hhplus.be.server.application.port.out.ReservationPort;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Reservation;
import kr.hhplus.be.server.domain.model.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("중복 예매 API 요청으로 인한 상태 꼬임 방지 통합 테스트")
class DuplicateReservationRequestIntegrationTest {

    @Autowired
    private ReservationUseCaseImpl reservationUseCase;

    @Autowired
    private ReservationPort reservationPort;

    @Autowired
    private SeatPort seatPort;

    private final Long seatId = 500L;
    private final UUID userId = UUID.randomUUID();
    private final LocalDateTime now = LocalDateTime.of(2025, 8, 8, 13, 0);

    @BeforeEach
    void setUp() {
        // 테스트 전 초기화 (좌석 저장)
        seatPort.save(new Seat(seatId, 900L, "Z9"));
    }

    @Test
    @DisplayName("동일한 유저가 동일 좌석에 대해 동시에 두 번 예약하면 한 번만 성공")
    void duplicateReservationRequests_shouldPreventStateCorruption() throws InterruptedException {
        int numberOfRequests = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numberOfRequests; i++) {
            executor.submit(() -> {
                try {
                    reservationUseCase.reserveSeat(seatId, userId, now);
                } catch (Throwable t) {
                    exceptions.add(t);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 검증: 예매 성공은 1건, 나머지는 예외
        Optional<Reservation> reservation = reservationPort.findBySeatId(seatId);
        assertThat(reservation).isPresent();
        assertThat(reservation.get().getUserId()).isEqualTo(userId);

        assertThat(exceptions.size()).isEqualTo(1);
        assertThat(exceptions.get(0)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 예약된 좌석입니다");
    }
}

