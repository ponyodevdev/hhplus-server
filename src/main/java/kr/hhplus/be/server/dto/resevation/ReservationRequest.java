package kr.hhplus.be.server.dto.resevation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record ReservationRequest(  @Schema(description = "공연 일정 ID", example = "11")
                                   Long optionId,

                                   @Schema(description = "좌석 ID", example = "102")
                                   Long seatId) {
}
