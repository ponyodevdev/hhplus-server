package kr.hhplus.be.server.application.port.in;

import java.util.UUID;

public interface SeatUseCase {
    void assignSeat(Long seatId, UUID userId);
}
