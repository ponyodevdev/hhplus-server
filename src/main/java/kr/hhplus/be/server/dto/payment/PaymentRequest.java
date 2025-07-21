package kr.hhplus.be.server.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentRequest(@Schema(description = "예약 ID", example = "10001")
                              Long reservationId,

                             @Schema(description = "결제 금액", example = "50000")
                              Integer amount) {
}
