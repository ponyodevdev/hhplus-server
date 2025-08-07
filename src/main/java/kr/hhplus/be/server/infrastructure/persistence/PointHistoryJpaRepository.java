package kr.hhplus.be.server.infrastructure.persistence;

import kr.hhplus.be.server.domain.model.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findAllByUserId(long userId);
}