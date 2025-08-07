package kr.hhplus.be.server.infrastructure.adapter.out;

import kr.hhplus.be.server.application.port.out.ReservationPort;
import kr.hhplus.be.server.domain.model.Reservation;
import kr.hhplus.be.server.infrastructure.persistence.ReservationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ReservationAdapter implements ReservationPort {

    private final ReservationJpaRepository reservationJpaRepository;

    public ReservationAdapter(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public void save(Reservation reservation) {
        reservationJpaRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findBySeatId(Long seatId) {
        return reservationJpaRepository.findBySeatId(seatId);
    }
}