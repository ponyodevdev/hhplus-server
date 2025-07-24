package kr.hhplus.be.server.domain.model;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConcertTest {

    @DisplayName("공연이 활성화된 경우 isActive는 true를 반환한다")
    @Test
    void activeConcertReturnsTrue() {
        Concert concert = new Concert(1L, "Rock Festival", "An epic concert", true);

        Assertions.assertThat(concert.isActive()).isTrue();
    }

    @DisplayName("공연이 비활성화된 경우 isActive는 false를 반환한다")
    @Test
    void inactiveConcertReturnsFalse() {
        Concert concert = new Concert(1L, "Rock Festival", "An epic concert", false);

        Assertions.assertThat(concert.isActive()).isFalse();
    }

    @DisplayName("공연 제목이 null이면 예외가 발생한다")
    @Test
    void nullTitleThrowsException() {
        Assertions.assertThatThrownBy(() ->
                new Concert(1L, null, "Description", true)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("공연 제목은 필수입니다.");
    }

    @DisplayName("공연 제목이 빈 문자열이면 예외가 발생한다")
    @Test
    void blankTitleThrowsException() {
        Assertions.assertThatThrownBy(() ->
                new Concert(1L, "   ", "Description", true)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("공연 제목은 필수입니다.");
    }

}
