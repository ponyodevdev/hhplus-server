package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.model.Queue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class QueueTest {
    
    Queue queue;
    
    @DisplayName("발급된 토큰은 지정된 TTL만큼 만료시간이 설정된다.")
    @Test
    void getTokenIssuedTime(){

        //given
        LocalDateTime now = LocalDateTime.now();
        Duration ttl = Duration.ofMinutes(5);
        queue =  new  Queue(UUID.randomUUID(), UUID.randomUUID(), now, ttl);

        //when
        LocalDateTime expiresAt = queue.getExpiresAt();

        //then
        Assertions.assertThat(expiresAt).isEqualTo(now.plus(ttl));
    }

    @DisplayName("만료시간이 지나면 isExpired는 true를 반환한다.")
    @Test
    void ExpiredToken(){

        //given
        LocalDateTime now = LocalDateTime.now();
        queue = new Queue(UUID.randomUUID(), UUID.randomUUID(), now.minusMinutes(6), Duration.ofMinutes(5));

        //when
        boolean expired = queue.isExpired(now);

        //then
        Assertions.assertThat(expired).isTrue();
    }

    @DisplayName("유효한 토큰은 isExpired가 false를 반환한다.")
    @Test
    void notExpiredBeforeTtl(){

        // given
        LocalDateTime now = LocalDateTime.now();
        Queue queue = new Queue(UUID.randomUUID(), UUID.randomUUID(), now.minusMinutes(1), Duration.ofMinutes(5));

        // when
        boolean expired = queue.isExpired(now);

        // then
        Assertions.assertThat(expired).isFalse();
    }

    @DisplayName("토큰의 잔여 대기 시간을 계산한다.")
    @Test
    void remainingWaitTime(){

        // given
        LocalDateTime now = LocalDateTime.now();
        Queue queue = new Queue(UUID.randomUUID(), UUID.randomUUID(), now, Duration.ofMinutes(5));

        // when
        Duration remaining = queue.remainingWaitTime(now.plusMinutes(2));

        // then
        Assertions.assertThat(remaining.toMinutes()).isEqualTo(3);
    }








        
        
}
