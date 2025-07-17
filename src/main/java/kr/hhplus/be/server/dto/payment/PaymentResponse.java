package kr.hhplus.be.server.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

public record PaymentResponse(@Schema(description = "결제 ID", example = "f6bb72a9-22cf-4eec-a869-38f91ec2d2d7")
                                String paymentId,

                              @Schema(description = "결제 금액", example = "50000")
                                Integer amount,

                              @Schema(description = "결제 상태", example = "SUCCESS")
                              String status) {
}
