package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.domain.model.TransactionType;
import kr.hhplus.be.server.fake.InMemoryPaymentPort;
import kr.hhplus.be.server.fake.InMemoryPointHistoryPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class PointExpirationServiceTest {
    private InMemoryPointHistoryPort pointHistoryPort;
    private InMemoryPaymentPort paymentPort;
    private PointExpirationService pointExpirationService;

    @BeforeEach
    void setUp() {
        pointHistoryPort = new InMemoryPointHistoryPort();
        paymentPort = new InMemoryPaymentPort();
        pointExpirationService = new PointExpirationService(paymentPort, pointHistoryPort);
    }

    @Test
    @DisplayName("1년이 지난 포인트는 소멸된다.")
    void expireOldPoints() {
        // given
        long userId = 1L;
        LocalDateTime now = LocalDateTime.of(2025, 7, 9, 12, 0);

        long oldMillis = now.minusYears(1).minusDays(1).toInstant(ZoneOffset.ofHours(9)).toEpochMilli();
        long recentMillis = now.minusMonths(6).toInstant(ZoneOffset.ofHours(9)).toEpochMilli();

        pointHistoryPort.insert(userId, 5000, TransactionType.CHARGE, oldMillis);
        pointHistoryPort.insert(userId, 2000, TransactionType.CHARGE, recentMillis);
        paymentPort.insertOrUpdate(userId, 7000);

        // when
        pointExpirationService.expirePointsForUsers(List.of(userId), now);

        // then
        Assertions.assertThat(paymentPort.selectById(userId).point()).isEqualTo(2000);

        Assertions.assertThat(pointHistoryPort.selectAllByUserId(userId))
                .anyMatch(h -> h.type() == TransactionType.EXPIRE && h.amount() == 5000);
    }

    @Test
    @DisplayName("1년이 지나지 않은 포인트는 소멸되지 않는다.")
    void recentPointsRemain() {
        // given
        long userId = 2L;
        LocalDateTime now = LocalDateTime.of(2025, 7, 9, 12, 0);

        long millis = now.minusMonths(3).toInstant(ZoneOffset.ofHours(9)).toEpochMilli();
        pointHistoryPort.insert(userId, 3000, TransactionType.CHARGE, millis);
        paymentPort.insertOrUpdate(userId, 3000);

        // when
        pointExpirationService.expirePointsForUsers(List.of(userId), now);

        // then
        Assertions.assertThat(paymentPort.selectById(userId).point()).isEqualTo(3000);

        Assertions.assertThat(pointHistoryPort.selectAllByUserId(userId))
                .noneMatch(h -> h.type() == TransactionType.EXPIRE);
    }
}
