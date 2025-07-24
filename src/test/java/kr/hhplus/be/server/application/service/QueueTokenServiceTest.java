package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.QueuePort;
import kr.hhplus.be.server.domain.model.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class QueueTokenServiceTest {

    @Mock
    QueuePort queuePort;

    @InjectMocks
    QueueTokenService queueTokenService;

    LocalDateTime fixedNow = LocalDateTime.of(2025, 7, 23, 12, 0);
    Duration tokenTTL = Duration.ofMinutes(10);
    Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(fixedNow.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        queueTokenService = new QueueTokenService(queuePort, tokenTTL, fixedClock);
    }

    @Test
    @DisplayName("대기열 토큰을 정상적으로 발급한다.")
    void issueToken_success() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        queueTokenService.issueToken(userId);

        // then
        ArgumentCaptor<Queue> captor = ArgumentCaptor.forClass(Queue.class);
        verify(queuePort).save(captor.capture());

        Queue saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getIssuedAt()).isEqualTo(fixedNow);
        assertThat(saved.getExpiresAt()).isEqualTo(fixedNow.plus(tokenTTL));
    }

    @Test
    @DisplayName("토큰이 유효하면 대기열 상태를 정상 조회한다.")
    void getTokenStatus_success() {
        UUID tokenId = UUID.randomUUID();
        Queue token = new Queue(tokenId, UUID.randomUUID(), fixedNow, tokenTTL);
        when(queuePort.findById(tokenId)).thenReturn(Optional.of(token));

        Queue result = queueTokenService.getTokenStatus(tokenId, fixedNow.plusMinutes(5));

        assertThat(result).isSameAs(token);
    }

    @Test
    @DisplayName("토큰이 만료되었으면 예외가 발생한다.")
    void getTokenStatus_expiredToken_throwsException() {
        UUID tokenId = UUID.randomUUID();
        Queue expiredToken = new Queue(tokenId, UUID.randomUUID(), fixedNow.minusMinutes(15), tokenTTL);
        when(queuePort.findById(tokenId)).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> queueTokenService.getTokenStatus(tokenId, fixedNow))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("대기열 토큰이 만료되었습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 토큰이면 예외가 발생한다.")
    void getTokenStatus_notFoundToken_throwsException() {
        UUID tokenId = UUID.randomUUID();
        when(queuePort.findById(tokenId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queueTokenService.getTokenStatus(tokenId, fixedNow))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("존재하지 않는 대기열 토큰입니다.");
    }
}