package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.SeatQueryUseCase;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SeatQueryService implements SeatQueryUseCase {

    private final SeatPort seatPort;
    private final Clock clock;

    public SeatQueryService(SeatPort seatPort, Clock clock) {
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
