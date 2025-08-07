package kr.hhplus.be.server.intergration;

import kr.hhplus.be.server.application.port.in.QueueTokenUseCaseImpl;
import kr.hhplus.be.server.application.port.out.QueuePort;
import kr.hhplus.be.server.application.service.QueueTokenDomainService;
import kr.hhplus.be.server.domain.model.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.NoSuchElementException;
import java.util.Optional;

import java.util.UUID;

@SpringBootTest
class QueueTokenIntegrationTest {

    @Autowired
    private QueueTokenDomainService domainService;

    @Autowired
    private QueuePort queuePort;

    private final Duration tokenTTL = Duration.ofMinutes(5);
    private final Clock fixedClock = Clock.fixed(Instant.parse("2025-08-04T21:00:00Z"), ZoneId.of("UTC"));

    private QueueTokenUseCaseImpl queueTokenUseCase;

    @BeforeEach
    void setUp() {
        queueTokenUseCase = new QueueTokenUseCaseImpl(domainService, tokenTTL, fixedClock, queuePort);
    }

    @Test
    @DisplayName("대기열 토큰을 발급하면 UUID가 생성되고 저장된다")
    void issueToken_success() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        UUID tokenId = queueTokenUseCase.issueToken(userId);

        // then
        assertThat(tokenId).isNotNull();
        Optional<Queue> saved = queuePort.findById(tokenId);
        assertThat(saved).isPresent();
        assertThat(saved.get().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("대기열 토큰 만료 여부를 판단하고 남은 시간을 반환한다")
    void getRemainingTime_beforeExpiration() {
        // given
        UUID userId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();
        LocalDateTime issuedAt = LocalDateTime.ofInstant(fixedClock.instant(), fixedClock.getZone());

        Queue token = new Queue(tokenId, userId, issuedAt, tokenTTL);
        queuePort.save(token);

        // when
        Duration remaining = queueTokenUseCase.getRemainingTime(tokenId, issuedAt.plusMinutes(2));

        // then
        assertThat(remaining).isEqualTo(Duration.ofMinutes(3));
    }

    @Test
    @DisplayName("만료된 토큰 조회 시 예외 발생")
    void getRemainingTime_afterExpiration_throws() {
        // given
        UUID userId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();
        LocalDateTime issuedAt = LocalDateTime.ofInstant(fixedClock.instant(), fixedClock.getZone());

        Queue expired = new Queue(tokenId, userId, issuedAt, tokenTTL);
        queuePort.save(expired);

        LocalDateTime afterExpiry = issuedAt.plusMinutes(6);

        // when & then
        assertThatThrownBy(() -> queueTokenUseCase.getRemainingTime(tokenId, afterExpiry))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("대기열 토큰이 만료되었습니다");
    }

    @Test
    @DisplayName("존재하지 않는 토큰 조회 시 예외 발생")
    void getRemainingTime_notFoundToken_throws() {
        // given
        UUID tokenId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> queueTokenUseCase.getRemainingTime(tokenId, LocalDateTime.now(fixedClock)))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("존재하지 않는 대기열 토큰");
    }
}
