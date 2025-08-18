package kr.hhplus.be.server.infrastructure.persistence;

import kr.hhplus.be.server.domain.model.ConcertOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertOptionJpaRepository extends JpaRepository<ConcertOption, Long> {
    List<ConcertOption> findByConcertId(long concertId);
}