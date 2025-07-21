package kr.hhplus.be.server.controller.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.dto.concert.ConcertOptionResponse;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class concertController {

    // 공연 일정 목록 조회
    @Operation(summary = "예약 가능한 공연 일정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공연 일정 조회 성공",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            array = @ArraySchema(schema = @Schema(implementation = ConcertOptionResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "optionId": 11,
                                                "concertId": 1,
                                                "concertDateTime": "2025-08-01T20:00:00",
                                                "price": 50000
                                              },
                                              {
                                                "optionId": 12,
                                                "concertId": 1,
                                                "concertDateTime": "2025-08-02T20:00:00",
                                                "price": 60000
                                              }
                                            ]
                                            """
                            )))
    })
    @GetMapping("/concerts/{concertId}/options")
    public ResponseEntity<List<ConcertOptionResponse>> getConcertOptions(
            @RequestHeader("X-Queue-Token") String token,
            @PathVariable Long concertId
    ) {
        List<ConcertOptionResponse> options = List.of(
                new ConcertOptionResponse(11L, concertId, LocalDateTime.of(2025, 8, 1, 20, 0), 50000),
                new ConcertOptionResponse(12L, concertId, LocalDateTime.of(2025, 8, 2, 20, 0), 60000)
        );
        return ResponseEntity.ok(options);
    }

    // 공연 좌석 정보 조회
    @Operation(summary = "공연 좌석 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좌석 정보 조회 성공",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            array = @ArraySchema(schema = @Schema(implementation = SeatInfoResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "seatId": 101,
                                                "optionId": 11,
                                                "seatLabel": "A1",
                                                "seatStatus": "AVAILABLE"
                                              },
                                              {
                                                "seatId": 102,
                                                "optionId": 11,
                                                "seatLabel": "A2",
                                                "seatStatus": "RESERVED"
                                              }
                                            ]
                                            """
                            )))
    })
    @GetMapping("/concert-options/{optionId}/seats")
    public ResponseEntity<List<SeatInfoResponse>> getSeatInfo(
            @RequestHeader("X-Queue-Token") String token,
            @PathVariable Long optionId
    ) {
        List<SeatInfoResponse> seats = List.of(
                new SeatInfoResponse(101L, optionId, "A1", "AVAILABLE"),
                new SeatInfoResponse(102L, optionId, "A2", "RESERVED")
        );
        return ResponseEntity.ok(seats);
    }

}
