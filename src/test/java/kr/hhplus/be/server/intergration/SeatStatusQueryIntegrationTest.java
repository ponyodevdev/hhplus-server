package kr.hhplus.be.server.intergration;

import kr.hhplus.be.server.application.port.in.SeatQueryUseCase;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.application.service.SeatQueryDomainService;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;
import kr.hhplus.be.server.domain.model.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeatStatusQueryIntegrationTest {

    @Autowired
    private SeatPort seatPort;

    @Autowired
    private ApplicationContext context;

    private Clock fixedClock = Clock.fixed(
            LocalDateTime.of(2025, 8, 4, 10, 0)
                    .toInstant(ZoneOffset.ofHours(9)),
            ZoneId.of("Asia/Seoul")
    );

    private SeatQueryDomainService seatQueryUseCase;

    @BeforeEach
    void setUp() {
        seatQueryUseCase = new SeatQueryDomainService(seatPort, fixedClock);
    }

    @Test
    @DisplayName("공연 옵션별 좌석 상태를 조회하면 RESERVED 또는 AVAILABLE 상태가 반환된다.")
    void getSeatInfoList_returnsCorrectStatusPerSeat() {
        // given
        Long optionId = 200L;
        LocalDateTime now = LocalDateTime.now(fixedClock);
        UUID userId = UUID.randomUUID();

        Seat reservedSeat = new Seat(1L, optionId, "A1");
        reservedSeat.assignTo(userId, now.minusMinutes(2)); // 아직 만료 안됨
        seatPort.save(reservedSeat);

        Seat availableSeat = new Seat(2L, optionId, "A2");
        seatPort.save(availableSeat);

        // when
        List<SeatInfoResponse> seatInfos = seatQueryUseCase.getSeatInfoList(optionId);

        // then
        assertThat(seatInfos).anySatisfy(seat -> {
            assertThat(seat.seatLabel()).isEqualTo("A1");
            assertThat(seat.seatStatus()).isEqualTo("RESERVED");
        });
        assertThat(seatInfos).anySatisfy(seat -> {
            assertThat(seat.seatLabel()).isEqualTo("A2");
            assertThat(seat.seatStatus()).isEqualTo("AVAILABLE");
        });
    }
}
