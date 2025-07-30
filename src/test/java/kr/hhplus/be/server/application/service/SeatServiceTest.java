package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    SeatPort seatPort;

    @InjectMocks
    SeatService seatService;

    Clock fixedClock;
    LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 7, 24, 12, 0);
        fixedClock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        seatService = new SeatService(seatPort, fixedClock);
    }

    @Test
    @DisplayName("사용자가 비어 있는 좌석을 점유할 수 있다")
    void assignAvailableSeat() {
        // given
        Long seatId = 1L;
        Seat seat = new Seat(seatId, 10L, "A1");
        when(seatPort.findById(seatId)).thenReturn(Optional.of(seat));

        UUID userId = UUID.randomUUID();

        // when
        seatService.assignSeat(seatId, userId);

        // then
        assertThat(seat.getOwnerId()).isEqualTo(userId);
        assertThat(seat.getExpiresAt()).isEqualTo(now.plusMinutes(5));
        verify(seatPort).save(seat);
    }

    @Test
    @DisplayName("만료되지 않은 좌석을 다른 사용자가 점유하면 예외 발생")
    void assignOccupiedSeatThrowsException() {
        Long seatId = 1L;
        UUID firstUser = UUID.randomUUID();
        UUID secondUser = UUID.randomUUID();

        Seat seat = new Seat(seatId, 10L, "A1");
        seat.assignTo(firstUser, now); // 점유됨
        when(seatPort.findById(seatId)).thenReturn(Optional.of(seat));

        assertThatThrownBy(() -> seatService.assignSeat(seatId, secondUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 다른 사용자가 좌석을 점유했습니다.");
    }

    @Test
    @DisplayName("점유가 만료된 좌석은 다른 사용자가 다시 점유 가능")
    void assignExpiredSeat() {
        Long seatId = 1L;
        UUID previousUser = UUID.randomUUID();
        UUID newUser = UUID.randomUUID();

        Seat seat = new Seat(seatId, 10L, "A1");
        seat.assignTo(previousUser, now.minusMinutes(10)); // 이미 만료됨
        when(seatPort.findById(seatId)).thenReturn(Optional.of(seat));

        seatService.assignSeat(seatId, newUser);
        assertThat(seat.getOwnerId()).isEqualTo(newUser);
    }
}
