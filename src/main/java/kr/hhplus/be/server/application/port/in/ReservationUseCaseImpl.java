package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.application.port.in.aop.DistributedLock;
import kr.hhplus.be.server.application.service.ReservationDomainService;
import kr.hhplus.be.server.domain.model.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReservationUseCaseImpl implements ReservationUseCase {

    private final ReservationDomainService reservationDomainService;

    public ReservationUseCaseImpl(ReservationDomainService reservationDomainService) {
        this.reservationDomainService = reservationDomainService;
    }

    @Override
    @DistributedLock(key = "'seat:' + #seatId", waitTime = 1, leaseTime = 3)
    public void reserveSeat(Long seatId, UUID userId, LocalDateTime now) {
        reservationDomainService.reserve(seatId, userId, now);
    }

    @Override
    public Reservation getReservationStatus(Long seatId, LocalDateTime now) {
        return reservationDomainService.findAndUpdateStatus(seatId, now);
    }
}
