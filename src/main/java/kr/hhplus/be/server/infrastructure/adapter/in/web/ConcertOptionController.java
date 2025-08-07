package kr.hhplus.be.server.infrastructure.adapter.in.web;

import kr.hhplus.be.server.application.port.in.ConcertOptionsUseCase;
import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/concert-options")
public class ConcertOptionController {

    private final ConcertOptionsUseCase concertOptionsUseCase;

    public ConcertOptionController(ConcertOptionsUseCase concertOptionsUseCase) {
        this.concertOptionsUseCase = concertOptionsUseCase;
    }

    @GetMapping("/reservable")
    public ResponseEntity<List<ConcertOptionResponse>> getReservableOptions() {
        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok(concertOptionsUseCase.getReservableConcertOptions(now));
    }
}