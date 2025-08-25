package kr.hhplus.be.server.infrastructure.client;

import kr.hhplus.be.server.domain.event.ReservationConfirmedEvent;
import org.springframework.stereotype.Component;

@Component
public class ExternalApiClient {

    public void sendReservation(ReservationConfirmedEvent event) {

        System.out.println("Mock API 전송됨: " + event);
    }
}