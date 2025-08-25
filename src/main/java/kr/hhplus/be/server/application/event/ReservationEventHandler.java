package kr.hhplus.be.server.application.event;

import kr.hhplus.be.server.domain.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.infrastructure.client.ExternalApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventHandler {

    private final ExternalApiClient externalApiClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationConfirmedEvent event) {
        externalApiClient.sendReservation(event);
    }
}