package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.dto.concert.SeatInfoResponse;

import java.util.List;

public interface SeatQueryUseCase {
    List<SeatInfoResponse> getSeatInfoList(Long optionId);
}
