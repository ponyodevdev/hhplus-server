package kr.hhplus.be.server.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

public class SeatTest {

    final LocalDateTime fixedNow = LocalDateTime.of(2025, 7, 24, 12, 0);

    @Test
    @DisplayName("좌석을 처음 점유하면 해당 사용자 ID가 설정된다.")
    void assignSeatToUser() {
        // given
        Seat seat = new Seat(1L, 1L, "A1");
        UUID userId = UUID.randomUUID();

        // when
        seat.assignTo(userId, fixedNow);

        // then
        Assertions.assertThat(seat.getOwnerId()).isEqualTo(userId);
        Assertions.assertThat(seat.isOccupied(fixedNow)).isTrue();
    }

    @Test
    @DisplayName("다른 사용자가 먼저 점유한 좌석은 점유할 수 없다.")
    void throwExceptionWhenSeatAlreadyOccupied() {
        // given
        Seat seat = new Seat(1L, 1L, "A1");
        UUID firstUser = UUID.randomUUID();
        UUID secondUser = UUID.randomUUID();
        seat.assignTo(firstUser, fixedNow);

        // when & then
        Assertions.assertThatThrownBy(() -> seat.assignTo(secondUser, fixedNow))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 다른 사용자가 좌석을 점유했습니다.");
    }

    @Test
    @DisplayName("처음 생성된 좌석은 비어 있다.")
    void seatIsInitiallyNotOccupied() {
        // given
        Seat seat = new Seat(1L, 1L, "A1");

        // then
        Assertions.assertThat(seat.isOccupied(fixedNow)).isFalse();
    }
}
