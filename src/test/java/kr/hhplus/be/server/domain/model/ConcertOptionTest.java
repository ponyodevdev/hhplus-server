package kr.hhplus.be.server.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class ConcertOptionTest {

    @DisplayName("현재 시간이 공연 시작 시간보다 이전이면 예약 가능하다")
    @Test
    void reservableBeforeStartTime() {
        LocalDateTime now = LocalDateTime.now();
        ConcertOption option = new ConcertOption(1L, 1L, now.plusMinutes(10), 50000);

        Assertions.assertThat(option.isReservable(now)).isTrue();
    }

    @DisplayName("현재 시간이 공연 시작 시간과 같거나 이후이면 예약 불가하다")
    @Test
    void notReservableAfterStartTime() {
        LocalDateTime now = LocalDateTime.now();
        ConcertOption option = new ConcertOption(1L, 1L, now, 50000);

        Assertions.assertThat(option.isReservable(now)).isFalse();
        Assertions.assertThat(option.isReservable(now.plusMinutes(1))).isFalse();
    }

    @DisplayName("음수 가격 입력 시 예외가 발생한다")
    @Test
    void negativePriceThrowsException() {
        Assertions.assertThatThrownBy(() ->
                new ConcertOption(1L, 1L, LocalDateTime.now().plusHours(1), -10000)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격");
    }

    @DisplayName("null 시작 시간 입력 시 예외가 발생한다")
    @Test
    void nullStartTimeThrowsException() {
        Assertions.assertThatThrownBy(() ->
                new ConcertOption(1L, 1L, null, 50000)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("공연 시작 시간");
    }

}