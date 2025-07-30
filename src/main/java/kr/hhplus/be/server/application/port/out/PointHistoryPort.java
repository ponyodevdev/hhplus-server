package kr.hhplus.be.server.application.port.out;

import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;

import java.util.List;

public interface PointHistoryPort {
    List<PointHistory> selectAllByUserId(long userId);
    void insert(long userId, long amount, TransactionType type, long timestamp);
}
