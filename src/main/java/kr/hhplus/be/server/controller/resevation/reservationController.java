package kr.hhplus.be.server.controller.resevation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kr.hhplus.be.server.dto.ErrorResponse;
import kr.hhplus.be.server.dto.resevation.ReservationRequest;
import kr.hhplus.be.server.dto.resevation.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class reservationController {

    // 좌석 예약 요청
    @Operation(summary = "좌석 예약 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "임시 예약 성공",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ReservationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "reservationId": "a9e18f31-dfa0-4e3b-8c72-5bd1bc149ec7",
                                              "expiresAt": "2025-07-17T18:10:00+09:00"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "errorCode": "E401",
                                              "message": "유효하지 않은 토큰입니다."
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "대기열 미통과",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "errorCode": "E403",
                                              "message": "아직 입장 순서가 아닙니다. 잠시만 기다려 주세요."
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "409", description = "좌석 중복 점유",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "errorCode": "E409",
                                              "message": "이미 예약된 좌석입니다."
                                            }
                                            """
                            )))
    })
    @PostMapping(value = "/reservations", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestHeader("X-Queue-Token") String token,
            @RequestBody ReservationRequest request
    ) {
        // 실제 로직 생략 - mock 응답
        ReservationResponse response = new ReservationResponse(
                UUID.randomUUID().toString(),
                OffsetDateTime.now(ZoneOffset.ofHours(9)).plusMinutes(5)
        );
        return ResponseEntity.ok(response);
    }
}
