package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.application.port.out.PointHistoryPort;
import kr.hhplus.be.server.application.service.ChargePointDomainService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChargePointUseCaseImpl implements ChargePointUseCase {

    private final ChargePointDomainService domainService;
    private final PointHistoryPort pointHistoryPort;

    public ChargePointUseCaseImpl(ChargePointDomainService domainService, PointHistoryPort pointHistoryPort) {
        this.domainService = domainService;
        this.pointHistoryPort = pointHistoryPort;
    }

    @Override
    public void charge(long userId, long amount, LocalDateTime now) {
        domainService.charge(userId, amount, now);
    }
}