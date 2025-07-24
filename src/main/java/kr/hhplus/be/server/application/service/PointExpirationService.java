package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.Payment;
import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class PointExpirationService {

    private final PaymentPort paymentPort;
    private final PointHistoryPort pointHistoryPort;

    public PointExpirationService(PaymentPort paymentPort,
                                  PointHistoryPort pointHistoryPort) {
        this.paymentPort = paymentPort;
        this.pointHistoryPort = pointHistoryPort;
    }

    public void expirePointsForUsers(List<Long> userIds, LocalDateTime now) {
        for (Long userId : userIds) {
            List<PointHistory> histories = pointHistoryPort.selectAllByUserId(userId);
            long expiredAmount = histories.stream()
                    .filter(h -> h.type() == TransactionType.CHARGE)
                    .filter(h -> isExpired(h.timestamp(), now))
                    .mapToLong(PointHistory::amount)
                    .sum();

            if (expiredAmount > 0) {
                Payment payment = paymentPort.selectById(userId);
                paymentPort.insertOrUpdate(userId, payment.point() - expiredAmount);
                pointHistoryPort.insert(userId, expiredAmount, TransactionType.EXPIRE,
                        now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli());
            }
        }
    }

    private boolean isExpired(long timestampMillis, LocalDateTime now) {
        LocalDateTime timestamp = LocalDateTime.ofEpochSecond(timestampMillis / 1000, 0, ZoneOffset.ofHours(9));
        return timestamp.isBefore(now.minusYears(1));
    }
}
