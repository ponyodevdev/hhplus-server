package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.model.PointHistory;

import java.util.List;

public interface PointHistoryUseCase {
    List<PointHistory> getHistories(long userId);
}
