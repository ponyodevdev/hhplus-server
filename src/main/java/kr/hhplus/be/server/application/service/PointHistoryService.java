package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.PointHistoryUseCase;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.PointHistory;

import java.util.List;

public class PointHistoryService implements PointHistoryUseCase {

    private final PointHistoryPort pointHistoryPort;

    public PointHistoryService(PointHistoryPort pointHistoryPort) {
        this.pointHistoryPort = pointHistoryPort;
    }

    @Override
    public List<PointHistory> getHistories(long userId) {
        return pointHistoryPort.selectAllByUserId(userId);
    }
}
