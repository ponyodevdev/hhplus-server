package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.ReservationPort;
import kr.hhplus.be.server.domain.model.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationDomainServiceTest {


    @Mock
    ReservationPort reservationPort;

    @InjectMocks
    ReservationDomainService reservationDomainService;

    final LocalDateTime fixedNow = LocalDateTime.of(2025, 7, 23, 12, 0);

    @Test
    @DisplayName("좌석이 예약되지 않은 경우 정상적으로 예약된다")
    void reserveSeat_success() {
        // given
        Long seatId = 1L;
        UUID userId = UUID.randomUUID();

        when(reservationPort.findBySeatId(seatId)).thenReturn(Optional.empty());

        // when
        reservationDomainService.reserve(seatId, userId, fixedNow);

        // then
        verify(reservationPort).save(any(Reservation.class));
    }

    @Test
    @DisplayName("좌석이 이미 예약되어 있고 만료되지 않았다면 예외 발생")
    void reserveSeat_alreadyReserved_notExpired() {
        // given
        Long seatId = 1L;
        UUID userId = UUID.randomUUID();
        Reservation existing = new Reservation(seatId, UUID.randomUUID(), fixedNow.minusMinutes(1));

        when(reservationPort.findBySeatId(seatId)).thenReturn(Optional.of(existing));

        // expect
        assertThatThrownBy(() -> reservationDomainService.reserve(seatId, userId, fixedNow))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 예약된 좌석입니다.");
    }

    @Test
    @DisplayName("예약된 좌석이 만료되었으면 새로 예약할 수 있다 (5분 경과)")
    void reserveSeat_expired_thenAllow() {
        // given
        Long seatId = 1L;
        UUID userId = UUID.randomUUID();

        // 기존 예약이 10분 전 = 5분 홀드가 끝난 상태
        Reservation expired = new Reservation(seatId, UUID.randomUUID(), fixedNow.minusMinutes(10));
        when(reservationPort.findBySeatId(seatId)).thenReturn(Optional.of(expired));

        // when
        reservationDomainService.reserve(seatId, userId, fixedNow);

        // then
        verify(reservationPort).save(any(Reservation.class));
    }

    @Test
    @DisplayName("좌석 예약 상태를 조회하면 만료 여부에 따라 상태가 업데이트되고 저장된다")
    void getReservationStatus_andUpdateIfExpired() {
        // given
        Long seatId = 1L;
        Reservation reservation = new Reservation(seatId, UUID.randomUUID(), fixedNow.minusMinutes(6)); // expired

        when(reservationPort.findBySeatId(seatId)).thenReturn(Optional.of(reservation));

        // when
        Reservation result = reservationDomainService.findAndUpdateStatus(seatId, fixedNow);

        // then
        assertThat(result.getStatus()).isEqualTo(Reservation.Status.EXPIRED);
        verify(reservationPort).save(reservation);
    }

    @Test
    @DisplayName("예약 정보가 없으면 예외 발생")
    void getReservationStatus_notFound() {
        // given
        Long seatId = 1L;
        when(reservationPort.findBySeatId(seatId)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> reservationDomainService.findAndUpdateStatus(seatId, fixedNow))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("예약 정보를 찾을 수 없습니다.");
    }
}