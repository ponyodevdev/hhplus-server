package kr.hhplus.be.server.intergration;

import kr.hhplus.be.server.application.port.in.ReservationUseCaseImpl;
import kr.hhplus.be.server.application.port.out.ReservationPort;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.application.service.ReservationDomainService;
import kr.hhplus.be.server.domain.model.Reservation;
import kr.hhplus.be.server.domain.model.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class SeatReservationIntegrationTest {

    @Autowired
    private SeatPort seatPort;

    @Autowired
    private ReservationPort reservationPort;

    @Autowired
    private ReservationUseCaseImpl reservationUseCase;

    private final LocalDateTime now = LocalDateTime.of(2025, 8, 4, 21, 0);

    @Test
    @DisplayName("좌석 예약 성공 시 좌석 점유 및 예약 정보 저장 확인")
    void reserveSeat_success() {
        Long seatId = 101L;
        UUID userId = UUID.randomUUID();

        reservationUseCase.reserveSeat(seatId, userId, now);

        Reservation reservation = reservationPort.findBySeatId(seatId).orElseThrow();
        assertThat(reservation.getUserId()).isEqualTo(userId);
        assertThat(reservation.getStatus()).isEqualTo(Reservation.Status.RESERVED);
    }

    @Test
    @DisplayName("동시 좌석 예약 시 이미 예약된 좌석이면 예외 발생")
    void reserveSeat_concurrentReservationFails() {
        Long seatId = 102L;
        UUID firstUser = UUID.randomUUID();
        UUID secondUser = UUID.randomUUID();

        // 첫 번째 예약 성공
        reservationUseCase.reserveSeat(seatId, firstUser, now);

        // 두 번째 예약 시도 → 실패
        assertThatThrownBy(() -> reservationUseCase.reserveSeat(seatId, secondUser, now))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 예약된 좌석입니다");
    }

    @Test
    @DisplayName("이미 예약된 좌석은 동일 유저라도 중복 예약 불가")
    void reserveSeat_duplicateBySameUserFails() {
        Long seatId = 103L;
        UUID userId = UUID.randomUUID();

        // 첫 예약
        reservationUseCase.reserveSeat(seatId, userId, now);

        // 동일 유저라도 두 번째 예약 → 실패
        assertThatThrownBy(() -> reservationUseCase.reserveSeat(seatId, userId, now))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 예약된 좌석입니다");
    }

    @Test
    @DisplayName("만료된 예약은 상태 갱신 후 조회 가능")
    void getReservationStatus_expiredThenUpdated() {
        Long seatId = 104L;
        UUID userId = UUID.randomUUID();
        LocalDateTime reservedAt = now.minusMinutes(6);

        // 사전 예약 넣기 (임시 reservation 직접 저장)
        reservationPort.save(new Reservation(seatId, userId, reservedAt));

        Reservation result = reservationUseCase.getReservationStatus(seatId, now);

        assertThat(result.getStatus()).isEqualTo(Reservation.Status.EXPIRED);
    }
}
