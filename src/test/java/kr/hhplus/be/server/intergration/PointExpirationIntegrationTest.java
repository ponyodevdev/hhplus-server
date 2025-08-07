package kr.hhplus.be.server.intergration;

import kr.hhplus.be.server.application.port.in.PaymentExpirationUseCaseImpl;
import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.Payment;
import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import static org.mockito.Mockito.*;

@SpringBootTest
class PointExpirationIntegrationTest{

    @Autowired
    private PointHistoryPort pointHistoryPort;

    @Autowired
    private PaymentPort paymentPort;

    @Autowired
    private PaymentExpirationUseCaseImpl useCase;


    @Test
    @DisplayName("1년 이상 지난 포인트 자동 차감 → 이력 및 잔액 확인")
    void shouldExpirePointsAfterOneYear() {
        // given
        long userId = 999L;  // 테스트용 유저 ID
        LocalDateTime now = LocalDateTime.of(2025, 8, 4, 0, 0);

        long charge1 = 5000;
        long charge2 = 2000;

        long charge1Epoch = now.minusYears(1).minusDays(1)
                .toInstant(ZoneOffset.ofHours(9)).toEpochMilli(); // 만료 대상
        long charge2Epoch = now.minusDays(100)
                .toInstant(ZoneOffset.ofHours(9)).toEpochMilli(); // 유지 대상

        pointHistoryPort.insert(userId, charge1, TransactionType.CHARGE, charge1Epoch);
        pointHistoryPort.insert(userId, charge2, TransactionType.CHARGE, charge2Epoch);
        paymentPort.insertOrUpdate(userId, charge1 + charge2);  // 7000 충전

        // when
        useCase.expirePointsForUsers(List.of(userId), now);

        // then
        // 현재 잔액 확인
        long updated = paymentPort.findPointByUserId(userId);
        assertThat(updated).isEqualTo(charge2);  // 7000 - 5000 = 2000

        // 만료 이력 존재 확인
        List<PointHistory> history = pointHistoryPort.selectAllByUserId(userId);
        assertThat(history).anySatisfy(ph -> {
            assertThat(ph.getAmount()).isEqualTo(charge1);
            assertThat(ph.getType()).isEqualTo(TransactionType.EXPIRE);
        });
    }


}
