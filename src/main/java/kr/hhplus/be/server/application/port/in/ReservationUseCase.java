package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReservationUseCase {
    void reserveSeat(Long seatId, UUID userId, LocalDateTime now);
    Reservation getReservationStatus(Long seatId, LocalDateTime now);
}
