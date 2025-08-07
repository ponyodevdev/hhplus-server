package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.SeatQueryUseCase;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class SeatQueryDomainService {

    private final SeatPort seatPort;
    private final Clock clock;

    public SeatQueryDomainService(SeatPort seatPort, Clock clock) {
        this.seatPort = seatPort;
        this.clock = clock;
    }

    public List<SeatInfoResponse> getSeatInfoList(Long optionId) {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Seat> seats = seatPort.findAllByOptionId(optionId);
        return seats.stream()
                .map(seat -> {
                    String status = seat.isOccupied(now) ? "RESERVED" : "AVAILABLE";
                    return new SeatInfoResponse(
                            seat.getSeatId(),
                            seat.getOptionId(),
                            seat.getSeatLabel(),
                            status
                    );
                })
                .toList();
    }
}