package kr.hhplus.be.server.dto.balance;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChargeRequest(@Schema(description = "충전할 금액", example = "20000")
                             int amount) {
}
