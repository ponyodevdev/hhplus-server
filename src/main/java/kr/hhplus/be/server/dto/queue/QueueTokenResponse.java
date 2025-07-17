package kr.hhplus.be.server.dto.queue;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "대기열 토큰 발급 응답")
public record QueueTokenResponse(
        @Schema(description = "대기열 정보")
        WaitingQueue waitingQueue
) {
    @Schema(description = "대기열")
    public record WaitingQueue(

            @Schema(description = "대기열 ID", example = "1001")
            Long id,

            @Schema(description = "콘서트 ID", example = "1")
            String concertId,

            @Schema(description = "UUID 형식의 토큰", example = "4a3e1d30-f9e3-11ed-be56-0242ac120002")
            String uuid,

            @Schema(description = "대기 상태", example = "WAITING")
            String status,

            @Schema(description = "만료 시간", example = "2025-07-17T18:10:00+09:00")
            OffsetDateTime expiredAt,

            @Schema(description = "생성 시간", example = "2025-07-17T18:00:00+09:00")
            OffsetDateTime createdAt,

            @Schema(description = "업데이트 시간", example = "2025-07-17T18:00:00+09:00")
            OffsetDateTime updatedAt
    ) {}
}
