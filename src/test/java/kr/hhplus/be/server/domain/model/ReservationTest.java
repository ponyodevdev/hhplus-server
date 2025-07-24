package kr.hhplus.be.server.domain.model.reservation;

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
    @DisplayName("updateStatus를 호출하지 않으면 상태는 그대로 유지된다.")
    void statusRemainsUnchangedWithoutUpdateStatusCall() {
        // given
        Reservation reservation = new Reservation(1L, UUID.randomUUID(), LocalDateTime.now());

        // then
        Assertions.assertThat(reservation.getStatus()).isEqualTo(Reservation.Status.RESERVED);
    }
}
