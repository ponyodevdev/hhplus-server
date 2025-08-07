package kr.hhplus.be.server.infrastructure.adapter.in.web;

import kr.hhplus.be.server.application.port.in.ChargePointUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/charge")
public class ChargePointController {

    private final ChargePointUseCase chargePointUseCase;

    public ChargePointController(ChargePointUseCase chargePointUseCase) {
        this.chargePointUseCase = chargePointUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> charge(@RequestParam long userId, @RequestParam long amount) {
        chargePointUseCase.charge(userId, amount, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
}