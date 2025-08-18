package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.ConcertOptionPort;
import kr.hhplus.be.server.domain.model.Concert;
import kr.hhplus.be.server.domain.model.ConcertOption;
import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertOptionQueryService {
    private final ConcertOptionPort concertPort;
    private final ConcertOptionPort concertOptionPort;

    @Cacheable(value = "optionsByConcert", key = "#concertId")
    public List<ConcertOption> getAllOptions(long concertId) {
        return concertOptionPort.findByConcertId(concertId);
    }

    public List<ConcertOptionResponse> getReservableOptions(long concertId, LocalDateTime now) {
        return getAllOptions(concertId).stream()
                .filter(opt -> opt.isReservable(now))
                .map(opt -> new ConcertOptionResponse(
                        opt.getId(), opt.getConcertId(), opt.getStartTime(), opt.getPrice()))
                .toList();
    }

}

