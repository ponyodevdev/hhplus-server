package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ChargePointUseCase;
import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.Charge;
import kr.hhplus.be.server.domain.model.TransactionType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ChargePointService implements ChargePointUseCase {

    private final PaymentPort paymentPort;
    private final PointHistoryPort pointHistoryPort;

    public ChargePointService(PaymentPort paymentPort, PointHistoryPort pointHistoryPort) {
        this.paymentPort = paymentPort;
        this.pointHistoryPort = pointHistoryPort;
    }

    @Override
    public void charge(long userId, long amount, LocalDateTime now) {
        if (!paymentPort.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        Charge charge = new Charge(paymentPort.findById(userId).point());
        charge.validateMaintenanceTime(now);
        charge.addPoint(amount);

        paymentPort.insertOrUpdate(userId, charge.getChargedPoint());

        pointHistoryPort.insert(
                userId,
                amount,
                TransactionType.CHARGE,
                now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
        );
    }
}
