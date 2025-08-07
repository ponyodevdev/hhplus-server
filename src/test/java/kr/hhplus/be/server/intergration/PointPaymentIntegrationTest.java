package kr.hhplus.be.server.intergration;

import kr.hhplus.be.server.application.port.in.ChargePointUseCaseImpl;
import kr.hhplus.be.server.application.port.in.PaymentUseCaseImpl;
import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.Payment;
import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PointPaymentIntegrationTest {

    @Autowired
    PaymentPort paymentPort;

    @Autowired
    PointHistoryPort pointHistoryPort;

    @Autowired
    ChargePointUseCaseImpl chargeUseCase;

    @Autowired
    PaymentUseCaseImpl paymentUseCase;

    Clock fixedClock = Clock.fixed(Instant.parse("2025-08-04T12:00:00Z"), ZoneId.of("UTC"));
    LocalDateTime now = LocalDateTime.now(fixedClock);

    @BeforeEach
    void clearTestData() {
        long userId = 42L;
        paymentPort.insertOrUpdate(userId, 0L);

    }

    @Test
    @DisplayName("충전 후 결제 성공 → 이력 및 잔액 확인")
    void shouldChargeAndPayPointAndRecordHistory() {
        // given
        long userId = 42L;

        // when
        chargeUseCase.charge(userId, 10000L, now); // 충전
        paymentUseCase.payment(userId, 3000L, now); // 결제

        // then
        long balance = paymentPort.findPointByUserId(userId);
        assertThat(balance).isEqualTo(7000L);

        long expectedEpoch = now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli();
        List<PointHistory> history = pointHistoryPort.selectAllByUserId(userId);

        assertThat(history).anySatisfy(h -> {
            assertThat(h.getAmount()).isEqualTo(10000L);
            assertThat(h.getType()).isEqualTo(TransactionType.CHARGE);
            assertThat(h.getTimestamp()).isEqualTo(expectedEpoch);
        });

        assertThat(history).anySatisfy(h -> {
            assertThat(h.getAmount()).isEqualTo(3000L);
            assertThat(h.getType()).isEqualTo(TransactionType.USE);
            assertThat(h.getTimestamp()).isEqualTo(expectedEpoch);
        });
    }

    @Test
    @DisplayName("포인트 부족 시 결제 실패")
    void shouldFailPaymentWhenInsufficientPoints() {
        // given
        long userId = 43L;
        paymentPort.insertOrUpdate(userId, 0L);
        chargeUseCase.charge(userId, 1000L, now);

        // when & then
        assertThatThrownBy(() -> paymentUseCase.payment(userId, 2000L, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔고가 부족합니다.");

        long balance = paymentPort.findPointByUserId(userId);
        assertThat(balance).isEqualTo(1000L); // 그대로 유지
    }
}
