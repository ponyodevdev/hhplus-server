package kr.hhplus.be.server.fake;

import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class InMemoryPointHistoryPort implements PointHistoryPort {
    private final List<PointHistory> histories = new ArrayList<>();

    @Override
    public void insert(long userId, long amount, TransactionType type, long timestamp) {
        histories.add(new PointHistory(userId, amount, type, timestamp));
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return histories.stream()
                .filter(h -> h.userId() == userId)
                .toList();
    }
}
