package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.SeatUseCase;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

public class SeatService implements SeatUseCase {

    private final SeatPort seatPort;

    private final Clock clock;

    public SeatService(SeatPort seatPort, Clock clock) {
        this.seatPort = seatPort;
        this.clock = clock;
    }

    @Override
    public void assignSeat(Long seatId, UUID userId) {
        Seat seat = seatPort.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("해당 좌석이 존재하지 않습니다."));

        seat.assignTo(userId, LocalDateTime.now(clock));
        seatPort.save(seat);
    }
}
