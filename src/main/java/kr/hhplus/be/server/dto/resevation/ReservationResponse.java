package kr.hhplus.be.server.dto.resevation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

public record ReservationResponse(@Schema(description = "예약 ID", example = "a9e18f31-dfa0-4e3b-8c72-5bd1bc149ec7")
                                  String reservationId,

                                  @Schema(description = "예약 만료 시간", example = "2025-07-17T18:10:00+09:00")
                                  OffsetDateTime expiresAt) {
}
