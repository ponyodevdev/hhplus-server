package kr.hhplus.be.server.infrastructure.persistence;

import kr.hhplus.be.server.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findBySeatId(Long seatId);
}
