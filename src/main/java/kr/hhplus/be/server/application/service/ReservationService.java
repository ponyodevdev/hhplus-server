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

    @Override
    public void reserveSeat(Long seatId, UUID userId, LocalDateTime now) {
        reservationPort.findBySeatId(seatId).ifPresent(existing -> {
            if (!existing.isExpired(now)) {
                throw new IllegalStateException("이미 예약된 좌석입니다.");
            }
        });

        Reservation reservation = new Reservation(seatId, userId, now);
        reservationPort.save(reservation);
    }

    @Override
    public Reservation getReservationStatus(Long seatId, LocalDateTime now) {
        Reservation reservation = reservationPort.findBySeatId(seatId)
                .orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));

        boolean updated = reservation.updateStatus(now);
        if (updated) {
            reservationPort.save(reservation);
        }

        return reservation;
    }
}
