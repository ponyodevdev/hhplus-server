package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(@Schema(description = "에러 코드", example = "E401")
                             String errorCode,

                            @Schema(description = "에러 메시지", example = "유효하지 않은 토큰입니다.")
                             String message) {
}
