package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ConcertOptionsUseCase;
import kr.hhplus.be.server.application.port.out.ConcertOptionPort;
import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ConcertOptionService implements ConcertOptionsUseCase {

    private final ConcertOptionPort concertOptionPort;

    public ConcertOptionService(ConcertOptionPort concertOptionPort) {
        this.concertOptionPort = concertOptionPort;
    }

    @Override
    public List<ConcertOptionResponse> getReservableConcertOptions(LocalDateTime now) {
        return concertOptionPort.findAll().stream()
                .filter(option -> option.isReservable(now))
                .map(option -> new ConcertOptionResponse(
                        option.getId(),
                        option.getConcertId(),
                        option.getStartTime(),
                        option.getPrice()))
                .collect(Collectors.toList());
    }
}
