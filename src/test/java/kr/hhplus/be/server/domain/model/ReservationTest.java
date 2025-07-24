package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.model.Reservation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationTest {

    @Test
    @DisplayName("예약이 생성되면 초기 상태는 RESERVED다.")
    void initialStateIsReserved() {
        // given
        Reservation reservation = new Reservation(1L, UUID.randomUUID(), LocalDateTime.now());

        // then
        Assertions.assertThat(reservation.getStatus()).isEqualTo(Reservation.Status.RESERVED);
    }

    @Test
    @DisplayName("예약 생성 후 5분 이내에는 상태가 RESERVED로 유지된다.")
    void stateRemainsReservedWithinFiveMinutes() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(1L, UUID.randomUUID(), now);

        // when
        reservation.updateStatus(now.plusMinutes(3));

        // then
        Assertions.assertThat(reservation.getStatus()).isEqualTo(Reservation.Status.RESERVED);
    }

    @Test
    @DisplayName("예약 생성 후 5분이 지나면 상태가 EXPIRED로 변경된다.")
    void stateBecomesExpiredAfterFiveMinutes() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(1L, UUID.randomUUID(), now);

        // when
        reservation.updateStatus(now.plusMinutes(6));

        // then
        Assertions.assertThat(reservation.getStatus()).isEqualTo(Reservation.Status.EXPIRED);
    }

    @Test
    @DisplayName("updateStatus를 호출하지 않으면 상태는 그대로 유지된다.")
    void statusRemainsUnchangedWithoutUpdateStatusCall() {
        // given
        Reservation reservation = new Reservation(1L, UUID.randomUUID(), LocalDateTime.now());

        // then
        Assertions.assertThat(reservation.getStatus()).isEqualTo(Reservation.Status.RESERVED);
    }
}
