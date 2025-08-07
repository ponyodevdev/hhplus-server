package kr.hhplus.be.server.infrastructure.adapter.out;

import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;
import kr.hhplus.be.server.infrastructure.persistence.PointHistoryJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointHistoryAdapter implements PointHistoryPort {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    public PointHistoryAdapter(PointHistoryJpaRepository pointHistoryJpaRepository) {
        this.pointHistoryJpaRepository = pointHistoryJpaRepository;
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return pointHistoryJpaRepository.findAllByUserId(userId);
    }

    @Override
    public void insert(long userId, long amount, TransactionType type, long timestamp) {
        PointHistory history = new PointHistory(userId, amount, type, timestamp);
        pointHistoryJpaRepository.save(history);
    }
}