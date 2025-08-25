package kr.hhplus.be.server.event;

import kr.hhplus.be.server.application.port.out.ReservationPort;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.application.service.ReservationDomainService;
import kr.hhplus.be.server.domain.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.domain.model.Seat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationDomainEventTest {
    @Mock
    SeatPort seatPort;

    @Mock
    ReservationPort reservationPort;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    ReservationDomainService reservationDomainService;

    @Test
    void 예약이_성공하면_이벤트가_발행된다() {
        // given
        Long seatId = 1L;
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Seat seat = new Seat(seatId, 100L, "A-1");
        when(seatPort.findWithLockBySeatId(seatId)).thenReturn(Optional.of(seat));

        // when
        reservationDomainService.reserve(seatId, userId, now);

        // then
        verify(eventPublisher, times(1))
                .publishEvent(any(ReservationConfirmedEvent.class));
    }
}
