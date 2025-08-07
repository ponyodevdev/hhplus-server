package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SeatUseCaseImpl implements SeatUseCase {

    private final SeatPort seatPort;
    private final Clock clock;

    public SeatUseCaseImpl(SeatPort seatPort, Clock clock) {
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
