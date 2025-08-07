package kr.hhplus.be.server.application.service;


import kr.hhplus.be.server.application.port.out.ReservationPort;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Reservation;
import kr.hhplus.be.server.domain.model.Seat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component

public class ReservationDomainService {

    private final SeatPort seatPort;
    private final ReservationPort reservationPort;

    public ReservationDomainService(SeatPort seatPort, ReservationPort reservationPort) {
        this.seatPort = seatPort;
        this.reservationPort = reservationPort;
    }

    @Transactional
    public void reserve(Long seatId, UUID userId, LocalDateTime now) {
        // 락 걸고 좌석 조회
        Seat seat = seatPort.findWithLockBySeatId(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        // 이미 점유된 좌석이라면 예외
        if (seat.isOccupied(now)) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }

        // 좌석 배정
        seat.assignTo(userId, now);
        seatPort.save(seat); // 업데이트 반영
        reservationPort.save(new Reservation(seatId, userId, now)); // 예약 테이블 저장
    }

    public Reservation findAndUpdateStatus(Long seatId, LocalDateTime now) {
        Reservation reservation = reservationPort.findBySeatId(seatId)
                .orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));

        boolean updated = reservation.updateStatus(now);
        if (updated) {
            reservationPort.save(reservation);
        }

        return reservation;
    }
}
