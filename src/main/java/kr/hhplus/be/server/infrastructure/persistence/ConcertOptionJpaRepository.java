package kr.hhplus.be.server.infrastructure.persistence;

import kr.hhplus.be.server.domain.model.ConcertOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertOptionJpaRepository extends JpaRepository<ConcertOption, Long> {
}