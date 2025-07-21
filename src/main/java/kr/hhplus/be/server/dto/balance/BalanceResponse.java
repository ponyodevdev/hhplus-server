package kr.hhplus.be.server.dto.balance;

import io.swagger.v3.oas.annotations.media.Schema;

public record BalanceResponse(@Schema(description = "잔액", example = "120000")
                              int balance) {
}
