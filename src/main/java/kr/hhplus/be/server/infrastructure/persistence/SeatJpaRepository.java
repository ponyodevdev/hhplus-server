package kr.hhplus.be.server.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByOptionId(Long optionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
    Optional<Seat> findWithLockBySeatId(@Param("seatId") Long seatId);
}
