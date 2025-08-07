package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatQueryUseCaseImpl implements SeatQueryUseCase{

    private final SeatPort seatPort;
    private final Clock clock;

    public SeatQueryUseCaseImpl(SeatPort seatPort, Clock clock) {
        this.seatPort = seatPort;
        this.clock = clock;
    }


      @Override
    public List<SeatInfoResponse> getSeatInfoList(Long optionId) {
        LocalDateTime now = LocalDateTime.now(clock);
        return seatPort.findAllByOptionId(optionId).stream()
                .map(seat -> new SeatInfoResponse(
                        seat.getSeatId(),
                        seat.getOptionId(),
                        seat.getSeatLabel(),
                        seat.isOccupied(now) ? "RESERVED" : "AVAILABLE"
                ))
                .collect(Collectors.toList());
    }
}
