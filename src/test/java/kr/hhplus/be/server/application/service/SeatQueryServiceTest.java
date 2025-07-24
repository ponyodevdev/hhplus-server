package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatQueryServiceTest {

    @Mock
    SeatPort seatPort;

    @InjectMocks
    SeatQueryService seatQueryService;

    Clock fixedClock;
    LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 7, 24, 12, 0);
        fixedClock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        seatQueryService = new SeatQueryService(seatPort, fixedClock);
    }

    @Test
    @DisplayName("예약 여부에 따라 좌석 상태를 정확히 반환한다")
    void returnsCorrectSeatStatus() {
        // given
        Seat available = new Seat(1L, 11L, "A1");
        Seat reserved = new Seat(2L, 11L, "A2");

        UUID userId = UUID.randomUUID();
        reserved.assignTo(userId, now); // ⬅️ 현재 시간 기준으로 예약

        when(seatPort.findAllByOptionId(11L)).thenReturn(List.of(available, reserved));

        // when
        List<SeatInfoResponse> result = seatQueryService.getSeatInfoList(11L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).seatStatus()).isEqualTo("AVAILABLE");
        assertThat(result.get(1).seatStatus()).isEqualTo("RESERVED");
    }

}