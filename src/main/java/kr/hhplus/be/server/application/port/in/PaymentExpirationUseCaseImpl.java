package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.application.service.PointExpirationDomainService;
import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class PaymentExpirationUseCaseImpl implements PointExpirationUseCase {

    private final PointExpirationDomainService domainService;
    private final PointHistoryPort pointHistoryPort;
    private final PaymentPort paymentPort;

    public PaymentExpirationUseCaseImpl(PointExpirationDomainService domainService, PointHistoryPort pointHistoryPort, PaymentPort paymentPort) {
        this.domainService = domainService;
        this.pointHistoryPort = pointHistoryPort;
        this.paymentPort = paymentPort;
    }

    @Override
    public void expirePointsForUsers(List<Long> userIds, LocalDateTime now) {
        for (Long userId : userIds) {
            List<PointHistory> histories = pointHistoryPort.selectAllByUserId(userId);

            long expiredAmount = domainService.calculateExpiredAmount(histories, now);

            if (expiredAmount > 0) {
                long current = paymentPort.findPointByUserId(userId);
                paymentPort.insertOrUpdate(userId, current - expiredAmount);
                pointHistoryPort.insert(
                        userId,
                        expiredAmount,
                        TransactionType.EXPIRE,
                        now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
                );
            }
        }
    }
}