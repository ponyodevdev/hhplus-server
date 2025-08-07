package kr.hhplus.be.server.infrastructure.adapter.in.web;

import kr.hhplus.be.server.application.port.in.SeatQueryUseCase;
import kr.hhplus.be.server.application.port.in.SeatUseCase;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/seats")
public class SeatController {

    private final SeatQueryUseCase seatQueryUseCase;
    private final SeatUseCase seatUseCase;

    public SeatController(SeatQueryUseCase seatQueryUseCase, SeatUseCase seatUseCase) {
        this.seatQueryUseCase = seatQueryUseCase;
        this.seatUseCase = seatUseCase;
    }

    @PostMapping("/{seatId}/assign")
    public ResponseEntity<Void> assignSeat(@PathVariable Long seatId, @RequestParam UUID userId) {
        seatUseCase.assignSeat(seatId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/option/{optionId}")
    public ResponseEntity<List<SeatInfoResponse>> getSeatInfoList(@PathVariable Long optionId) {
        return ResponseEntity.ok(seatQueryUseCase.getSeatInfoList(optionId));
    }
}
