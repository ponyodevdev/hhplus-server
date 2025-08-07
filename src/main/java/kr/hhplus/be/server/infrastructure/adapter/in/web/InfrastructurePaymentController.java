package kr.hhplus.be.server.infrastructure.adapter.in.web;

import kr.hhplus.be.server.application.port.in.PaymentUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/payments")
public class InfrastructurePaymentController {

    private final PaymentUseCase paymentUseCase;

    public InfrastructurePaymentController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> pay(
            @RequestParam long userId,
            @RequestParam long amount
    ) {
        paymentUseCase.payment(userId, amount, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
}
