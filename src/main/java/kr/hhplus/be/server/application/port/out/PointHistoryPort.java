package kr.hhplus.be.server.application.port.out;

import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;

import java.util.List;

public interface PointHistoryPort {
    void insert(long userId, long amount, TransactionType type, long timestamp);
    List<PointHistory> selectAllByUserId(long userId);
}
