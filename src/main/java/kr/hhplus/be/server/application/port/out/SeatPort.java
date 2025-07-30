package kr.hhplus.be.server.application.port.out;

import kr.hhplus.be.server.domain.model.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatPort {
    Optional<Seat> findById(Long seatId);
    List<Seat> findAllByOptionId(Long optionId);
    void save(Seat seat);
}
