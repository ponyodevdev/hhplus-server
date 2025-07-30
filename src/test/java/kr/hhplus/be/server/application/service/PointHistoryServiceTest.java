package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;
import kr.hhplus.be.server.fake.InMemoryPointHistoryPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PointHistoryServiceTest {

    @DisplayName("유저의 포인트 히스토리를 모두 조회한다.")
    @Test
    void getAllPointHistories() {
        // given
        long userId = 1L;
        InMemoryPointHistoryPort pointHistoryPort = new InMemoryPointHistoryPort();
        PointHistoryService pointHistoryService = new PointHistoryService(pointHistoryPort);

        long now = System.currentTimeMillis();
        pointHistoryPort.insert(userId, 1000, TransactionType.CHARGE, now);
        pointHistoryPort.insert(userId, 500, TransactionType.USE, now);
        pointHistoryPort.insert(userId, 300, TransactionType.EXPIRE, now);

        // when
        List<PointHistory> histories = pointHistoryService.getHistories(userId);

        // then
        Assertions.assertThat(histories).hasSize(3);
        Assertions.assertThat(histories)
                .extracting(PointHistory::type)
                .containsExactlyInAnyOrder(TransactionType.CHARGE, TransactionType.USE, TransactionType.EXPIRE);
    }

}
