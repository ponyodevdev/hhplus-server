package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.out.ConcertOptionPort;
import kr.hhplus.be.server.domain.model.ConcertOption;
import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcertOptionDomainServiceTest {


    @Mock
    ConcertOptionPort concertOptionPort;

    @InjectMocks
    ConcertOptionDomainService concertOptionDomainService;

    LocalDateTime fixedNow = LocalDateTime.of(2025, 7, 23, 12, 0);

    @DisplayName("현재 시간 기준으로 예약 가능한 공연 일정만 필터링하여 반환한다")
    @Test
    void onlyReservableOptionsAreReturned() {
        // given
        ConcertOption futureOption = new ConcertOption(1L, 1L, fixedNow.plusHours(1), 50000);
        ConcertOption pastOption = new ConcertOption(2L, 1L, fixedNow.minusMinutes(1), 50000);

        when(concertOptionPort.findAll()).thenReturn(List.of(futureOption, pastOption));

        // when
        List<ConcertOptionResponse> result = concertOptionDomainService.getReservableOptions(fixedNow);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).optionId()).isEqualTo(1L);
        verify(concertOptionPort).findAll();
    }

    @DisplayName("예약 가능한 공연이 없으면 빈 리스트 반환한다")
    @Test
    void returnEmptyListWhenNoReservableOptions() {
        // given
        ConcertOption pastOption = new ConcertOption(2L, 1L, fixedNow.minusMinutes(1), 50000);
        when(concertOptionPort.findAll()).thenReturn(List.of(pastOption));

        // when
        List<ConcertOptionResponse> result = concertOptionDomainService.getReservableOptions(fixedNow);

        // then
        assertThat(result).isEmpty();
        verify(concertOptionPort).findAll();
    }
}