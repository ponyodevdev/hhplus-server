package kr.hhplus.be.server.controller.balance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.dto.ErrorResponse;
import kr.hhplus.be.server.dto.balance.BalanceResponse;
import kr.hhplus.be.server.dto.balance.ChargeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class balanceController {
    //  잔액 조회
    @Operation(summary = "잔액 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 조회 성공",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = BalanceResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "balance": 100000
                                        }
                                        """
                            ))),

            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "errorCode": "E404",
                                          "message": "사용자를 찾을 수 없습니다."
                                        }
                                        """
                            )))
    })
    @GetMapping(value = "/users/{userId}/balance", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String userId) {
        return ResponseEntity.ok(new BalanceResponse(100_000));
    }

    //  잔액 충전
    @Operation(summary = "잔액 충전")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "충전 성공",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = BalanceResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "balance": 120000
                                        }
                                        """
                            ))),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 (음수 금액 등)",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "errorCode": "E400",
                                          "message": "충전 금액은 0보다 커야 합니다."
                                        }
                                        """
                            ))),

            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "errorCode": "E404",
                                          "message": "사용자를 찾을 수 없습니다."
                                        }
                                        """
                            )))
    })
    @PostMapping(value = "/users/{userId}/balance/charge", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BalanceResponse> chargeBalance(
            @PathVariable String userId,
            @RequestBody ChargeRequest request
    ) {
        return ResponseEntity.ok(new BalanceResponse(100_000 + request.amount()));
    }
}
