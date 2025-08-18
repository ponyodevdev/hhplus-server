package kr.hhplus.be.server.infrastructure.adapter.in.web;

import kr.hhplus.be.server.application.port.in.ConcertOptionsUseCase;
import kr.hhplus.be.server.application.service.ConcertOptionQueryService;
import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert-options")
public class ConcertOptionController {

    private final ConcertOptionsUseCase concertOptionsUseCase;
    private final ConcertOptionQueryService concertOptionQueryService;


    @GetMapping("/reservable")
    public ResponseEntity<List<ConcertOptionResponse>> getReservableOptions() {
        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok(concertOptionsUseCase.getReservableConcertOptions(now));
    }

    @GetMapping("/concerts/{concertId}/reservable-options")
    public List<ConcertOptionResponse> getReservableOptions(
            @PathVariable long concertId) {
        return concertOptionQueryService.getReservableOptions(concertId, LocalDateTime.now());
    }
}