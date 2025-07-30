package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertOptionsUseCase {
    List<ConcertOptionResponse> getReservableConcertOptions(LocalDateTime now);
}
