package kr.hhplus.be.server.infrastructure.adapter.out;

import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;
import kr.hhplus.be.server.infrastructure.persistence.SeatJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SeatAdapter implements SeatPort {

    private final SeatJpaRepository seatJpaRepository;

    public SeatAdapter(SeatJpaRepository seatJpaRepository) {
        this.seatJpaRepository = seatJpaRepository;
    }

    @Override
    public Optional<Seat> findById(Long seatId) {
        return seatJpaRepository.findById(seatId);
    }

    @Override
    public List<Seat> findAllByOptionId(Long optionId) {
        return seatJpaRepository.findAllByOptionId(optionId);
    }

    @Override
    public void save(Seat seat) {
        seatJpaRepository.save(seat);
    }

    @Override
    public Optional<Seat> findWithLockBySeatId(Long seatId) {
        return seatJpaRepository.findWithLockBySeatId(seatId);
    }
}