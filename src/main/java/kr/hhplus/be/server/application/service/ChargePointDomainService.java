package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.Charge;
import kr.hhplus.be.server.domain.model.TransactionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class ChargePointDomainService {

    private final PaymentPort paymentPort;
    private final PointHistoryPort pointHistoryPort;

    public ChargePointDomainService(PaymentPort paymentPort, PointHistoryPort pointHistoryPort) {
        this.paymentPort = paymentPort;
        this.pointHistoryPort = pointHistoryPort;
    }

    public long charge(long userId, long amount, LocalDateTime now) {
        if (!paymentPort.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        long current = paymentPort.findPointByUserId(userId);
        Charge charge = new Charge(current);
        charge.validateMaintenanceTime(now);
        charge.addPoint(amount);

        long updated = charge.getChargedPoint();
        paymentPort.insertOrUpdate(userId, updated);

        long epochMillis = now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli();
        pointHistoryPort.insert(userId, amount, TransactionType.CHARGE, epochMillis);

        return updated;
    }
}
