package kr.hhplus.be.server.controller.queue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.dto.ErrorResponse;
import kr.hhplus.be.server.dto.queue.QueueStatusResponse;
import kr.hhplus.be.server.dto.queue.QueueTokenRequest;
import kr.hhplus.be.server.dto.queue.QueueTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class queueController {

    // 유저 대기열 토큰 발급
    @Operation(summary = "대기열 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공",
                    content = @Content(
                            mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = QueueTokenResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                      "waitingQueue": {
                        "id": 1001,
                        "concertId": 1,
                        "uuid": "4a3e1d30-f9e3-11ed-be56-0242ac120002",
                        "status": "WAITING",
                        "expiredAt": "2025-07-17T18:10:00+09:00",
                        "createdAt": "2025-07-17T18:00:00+09:00",
                        "updatedAt": "2025-07-17T18:00:00+09:00"
                      }
                    }
                    """
                            )
                    )
            ),

            @ApiResponse(responseCode = "403", description = "대기열 진입 제한",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "errorCode": "E403",
                                          "message": "현재 입장 대기 인원이 많아 접속이 어려운 상태입니다. 잠시 후 다시 시도해 주세요."
                                        }
                                        """
                            )))
    })
    @PostMapping(value = "/queue/waitingToken", produces = "application/json;charset=UTF-8")
    public ResponseEntity<QueueTokenResponse> waitingToken(@RequestBody QueueTokenRequest request) {
        QueueTokenResponse.WaitingQueue waitingQueue = new QueueTokenResponse.WaitingQueue(
                1001L,
                request.concertId(),
                UUID.randomUUID().toString(),
                "WAITING",
                OffsetDateTime.now().plusMinutes(10),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new QueueTokenResponse(waitingQueue));
    }

    // 대기열 조회
    @Operation(summary = "대기열 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대기열 상태 조회 성공",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = QueueStatusResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "queueNumber": 7,
                                      "estimatedWaitTimeSeconds": 120,
                                      "status": "WAITING"
                                    }
                                    """
                            ))),

            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 누락 또는 유효하지 않음)",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "errorCode": "E401",
                                      "message": "입장 대기 정보가 확인되지 않아요. 새로고침 후 다시 시도해 주세요."
                                    }
                                    """
                            ))),

            @ApiResponse(responseCode = "403", description = "대기열 미통과 (아직 순서 아님)",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "errorCode": "E403",
                                      "message": "아직 입장 순서가 아니에요. 조금만 더 기다려 주세요."
                                    }
                                    """
                            )))
    })
    @GetMapping(value = "/queue", produces = "application/json;charset=UTF-8")
    public ResponseEntity<QueueStatusResponse> getQueueStatus(@RequestHeader("X-Queue-Token") String token) {
        // MOCK 로직
        QueueStatusResponse response = new QueueStatusResponse(
                new Random().nextInt(100) + 1,
                new Random().nextInt(300),
                "WAITING"
        );
        return ResponseEntity.ok(response);
    }

}
