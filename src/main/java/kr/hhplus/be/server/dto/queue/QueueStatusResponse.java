package kr.hhplus.be.server.dto.queue;

import io.swagger.v3.oas.annotations.media.Schema;

public record QueueStatusResponse(@Schema(description = "현재 대기 순서", example = "8")
                                    int queueNumber,

                                  @Schema(description = "예상 대기 시간 (초)", example = "180")
                                    int estimatedWaitTimeSeconds,

                                  @Schema(description = "대기 상태", example = "WAITING")
                                  String status) {
}
