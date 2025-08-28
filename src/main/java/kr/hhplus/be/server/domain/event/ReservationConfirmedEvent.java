package kr.hhplus.be.server.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationConfirmedEvent(
        Long reservationId,
        Long seatId,
        UUID userId,
        LocalDateTime reservedAt
) {}