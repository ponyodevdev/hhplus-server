package kr.hhplus.be.server.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ConcertOptionResponse( @Schema(description = "공연 일정 ID", example = "11")
                                     Long optionId,

                                     @Schema(description = "공연 ID", example = "1")
                                     Long concertId,

                                     @Schema(description = "공연 일시", example = "2025-08-01T20:00:00")
                                     LocalDateTime concertDateTime,

                                     @Schema(description = "가격", example = "50000")
                                     Integer price) {
}
