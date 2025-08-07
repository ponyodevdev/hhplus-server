package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.application.service.ConcertOptionDomainService;
import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConcertOptionsUseCaseImpl implements ConcertOptionsUseCase {

    private final ConcertOptionDomainService concertOptionDomainService;

    public ConcertOptionsUseCaseImpl(ConcertOptionDomainService concertOptionDomainService) {
        this.concertOptionDomainService = concertOptionDomainService;
    }

    @Override
    public List<ConcertOptionResponse> getReservableConcertOptions(LocalDateTime now) {
        return concertOptionDomainService.getReservableOptions(now);
    }
}
