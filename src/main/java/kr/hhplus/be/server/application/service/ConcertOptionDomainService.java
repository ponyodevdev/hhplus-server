package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.ConcertOptionPort;
import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConcertOptionDomainService {

    private final ConcertOptionPort concertOptionPort;

    public ConcertOptionDomainService(ConcertOptionPort concertOptionPort) {
        this.concertOptionPort = concertOptionPort;
    }

    public List<ConcertOptionResponse> getReservableOptions(LocalDateTime now) {
        return concertOptionPort.findAll().stream()
                .filter(option -> option.isReservable(now))
                .map(option -> new ConcertOptionResponse(
                        option.getId(),
                        option.getConcertId(),
                        option.getStartTime(),
                        option.getPrice()
                ))
                .collect(Collectors.toList());
    }
}
