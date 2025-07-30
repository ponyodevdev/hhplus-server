package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.domain.model.TransactionType;
import kr.hhplus.be.server.fake.InMemoryPaymentPort;
import kr.hhplus.be.server.fake.InMemoryPointHistoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

class ChargePointServiceTest {

    private ChargePointService chargePointService;

    private InMemoryPaymentPort userPointPort;
    private InMemoryPointHistoryPort pointHistoryPort;

    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        userPointPort = new InMemoryPaymentPort();
        pointHistoryPort = new InMemoryPointHistoryPort();
        chargePointService = new ChargePointService(userPointPort, pointHistoryPort);
    }

    @Test
    @DisplayName("존재하지 않는 유저는 충전할 수 없기에 예외가 발생한다.")
    void chargeFailUserNotFound() {
        assertThatThrownBy(() -> chargePointService.charge(99L, 10000, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }

    @Test
    @DisplayName("정상 충전 : 10,000원을 충전하면 10,000포인트가 적립된다.")
    void successCharge() {
        pointHistoryPort.insert(userId, 0, TransactionType.CHARGE, System.currentTimeMillis());
        userPointPort.insertOrUpdate(userId, 0);

        chargePointService.charge(userId, 10000, LocalDateTime.of(2025, 7, 9, 10, 0));

        long result = userPointPort.findById(userId).point();
        assertThat(result).isEqualTo(10000);
    }

    @Test
    @DisplayName("포인트 충전 시 보유 포인트가 1,000,000 포인트 초과되면 예외가 발생한다.")
    void chargePointExceedMax() {
        userPointPort.insertOrUpdate(userId, 999_000);
        pointHistoryPort.insert(userId, 0, TransactionType.CHARGE, System.currentTimeMillis());

        assertThatThrownBy(() ->
                        chargePointService.charge(userId, 2_000, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("총 보유 포인트는 최대 1,000,000 포인트를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("카드사 점검시간에는 충전할 수 없다")
    void chargePoint_maintenanceTime() {
        userPointPort.insertOrUpdate(userId, 0);
        pointHistoryPort.insert(userId, 0, TransactionType.CHARGE, System.currentTimeMillis());

        LocalDateTime maintenanceTime = LocalDateTime.of(2025, 7, 10, 23, 50);

        assertThatThrownBy(() ->
                        chargePointService.charge(userId, 1_000, maintenanceTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지금은 카드사 점검 시간입니다. 잠시 후 다시 시도해주세요.");
    }

    @Test
    @DisplayName("사용자가 충전 시 히스토리가 기록된다.")
    void chargePoint_historySaved() {
        userPointPort.insertOrUpdate(userId, 0);
        pointHistoryPort.insert(userId, 0, TransactionType.CHARGE, System.currentTimeMillis());

        LocalDateTime now = LocalDateTime.of(2025, 7, 9, 10, 0);
        chargePointService.charge(userId, 5_000, now);

        assertThat(pointHistoryPort.selectAllByUserId(userId))
                .anyMatch(h -> h.amount() == 5_000 && h.type() == TransactionType.CHARGE);
    }
}