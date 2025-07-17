package kr.hhplus.be.server.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.dto.ErrorResponse;
import kr.hhplus.be.server.dto.payment.PaymentRequest;
import kr.hhplus.be.server.dto.payment.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class paymentController {

    //  결제 API
    @Operation(summary = "결제 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 성공",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = PaymentResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "paymentId": "f6bb72a9-22cf-4eec-a869-38f91ec2d2d7",
                                          "amount" : "50000",
                                          "status": "SUCCESS"
                                        }
                                        """
                            ))),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 (포인트 부족 또는 예약 정보 누락)",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "errorCode": "E400",
                                          "message": "결제 요청 정보가 잘못되었습니다."
                                        }
                                        """
                            ))),

            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음 또는 만료)",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "errorCode": "E401",
                                          "message": "유효하지 않은 사용자 인증입니다."
                                        }
                                        """
                            )))
    })
    @PostMapping(value = "/payments", produces = "application/json;charset=UTF-8")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request,
                                                          @RequestHeader("X-Queue-Token") String token) {
        PaymentResponse response = new PaymentResponse(
                UUID.randomUUID().toString(),
                request.amount(),
                "SUCCESS"
        );
        return ResponseEntity.ok(response);
    }
}
