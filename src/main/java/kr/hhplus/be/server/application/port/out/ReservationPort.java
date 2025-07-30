package kr.hhplus.be.server.application.port.out;

import kr.hhplus.be.server.domain.model.Reservation;

import java.util.Optional;

public interface ReservationPort {
    void save(Reservation reservation);
    Optional<Reservation> findBySeatId(Long seatId);
}
