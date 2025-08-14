package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.dto.concert.SeatInfoResponse;

import java.util.List;
import java.util.UUID;

public interface SeatUseCase {
    void assignSeat(Long seatId, UUID userId);
    void cancelSeat(Long seatId);
}
