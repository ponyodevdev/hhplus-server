package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ReservationUseCase;
import kr.hhplus.be.server.application.port.out.ReservationPort;
import kr.hhplus.be.server.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

public class ReservationService implements ReservationUseCase {
    private final ReservationPort reservationPort;

    public ReservationService(ReservationPort reservationPort) {
        this.reservationPort = reservationPort;
    }
}
