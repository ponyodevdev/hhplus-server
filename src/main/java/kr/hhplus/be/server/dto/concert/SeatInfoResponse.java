package kr.hhplus.be.server.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;

public record SeatInfoResponse(@Schema(description = "좌석 ID", example = "101")
                                 Long seatId,

                               @Schema(description = "공연 일정 ID", example = "11")
                                 Long optionId,

                               @Schema(description = "좌석 번호", example = "A1")
                                 String seatLabel,

                               @Schema(description = "좌석 상태 (AVAILABLE | RESERVED)", example = "AVAILABLE")
                                 String seatStatus) {
}
