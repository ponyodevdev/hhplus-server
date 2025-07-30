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
class ReservationServiceTest {


    @Mock
    ReservationPort reservationPort;

    @InjectMocks
    ReservationService reservationService;

    final LocalDateTime fixedNow = LocalDateTime.of(2025, 7, 23, 12, 0);


    @Test
    @DisplayName("좌석이 이미 예약되어 있고 만료되지 않았다면 예외 발생")
    void reserveSeat_alreadyReserved_notExpired() {
        // given
        Long seatId = 1L;
        UUID userId = UUID.randomUUID();
        Reservation existing = new Reservation(seatId, UUID.randomUUID(), fixedNow.minusMinutes(1));

        when(reservationPort.findBySeatId(seatId)).thenReturn(Optional.of(existing));

        // expect
        assertThatThrownBy(() -> reservationService.reserveSeat(seatId, userId, fixedNow))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 예약된 좌석입니다.");
    }

    @Test
    @DisplayName("예약 정보가 없으면 예외 발생")
    void getReservationStatus_notFound() {
        // given
        Long seatId = 1L;
        when(reservationPort.findBySeatId(seatId)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> reservationService.getReservationStatus(seatId, fixedNow))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("예약 정보를 찾을 수 없습니다.");
    }
}