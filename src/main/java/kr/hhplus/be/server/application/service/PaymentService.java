package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.PaymentUseCase;
import kr.hhplus.be.server.application.port.out.PaymentPort;
import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.domain.model.Payment;
import kr.hhplus.be.server.domain.model.TransactionType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PaymentService implements PaymentUseCase {

    private final PaymentPort paymentPort;
    private final PointHistoryPort pointHistoryPort;

    public PaymentService(PaymentPort paymentPort, PointHistoryPort pointHistoryPort) {
        this.paymentPort = paymentPort;
        this.pointHistoryPort = pointHistoryPort;
    }

    @Override
    public void payment(long userId, long amount, LocalDateTime now) {
        // 유저 존재 확인
        if (!paymentPort.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        long currentPoint = paymentPort.findPointByUserId(userId);

        Payment payment = new Payment();
        payment.usePoint(amount, currentPoint);

        paymentPort.insertOrUpdate(userId, currentPoint - amount);

        pointHistoryPort.insert(userId, amount, TransactionType.USE, now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli());
    }

}
