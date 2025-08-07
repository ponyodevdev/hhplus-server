package kr.hhplus.be.server.infrastructure.adapter.in.web;

import kr.hhplus.be.server.application.port.in.ReservationUseCase;
import kr.hhplus.be.server.domain.model.Reservation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
public class InfrastructureReservationController {

    private final ReservationUseCase reservationUseCase;

    public InfrastructureReservationController(ReservationUseCase reservationUseCase) {
        this.reservationUseCase = reservationUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> reserveSeat(@RequestParam Long seatId, @RequestParam UUID userId) {
        reservationUseCase.reserveSeat(seatId, userId, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<Reservation> getReservationStatus(@PathVariable Long seatId) {
        Reservation reservation = reservationUseCase.getReservationStatus(seatId, LocalDateTime.now());
        return ResponseEntity.ok(reservation);
    }
}
