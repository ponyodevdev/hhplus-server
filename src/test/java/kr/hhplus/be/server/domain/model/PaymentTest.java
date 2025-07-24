package kr.hhplus.be.server.domain.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PaymentTest {

    Payment payment;

    @BeforeEach
    void setUp(){
        payment = new Payment();
    }


    @DisplayName("100포인트를 사용하면 정상적으로 사용된다.")
    @Test
    void useValidPoint(){
        int amountToUse = 100;
        int currentBalance = 1000;
        payment.usePoint(amountToUse, currentBalance);
        Assertions.assertEquals(100, payment.getUsedPoint());

    }

    @DisplayName("포인트 사용시 100포인트 단위가 아닌 경우 예외가 발생한다.")
    @Test
    void invalidUnitPoint(){
        assertUseThrows(99, "포인트는 100포인트 단위로만 사용할 수 있습니다.");
    }

    @DisplayName("잔고를 초과한 포인트를 사용하려고 하면 예외가 발생한다.")
    @Test
    void insufficientBalance(){
        assertUseThrows(1100, "잔고가 부족합니다.");
    }

    @DisplayName("0 포인트를 사용하려고 하면 예외가 발생한다.")
    @Test
    void useZeroPoint(){
        assertUseThrows(0, "사용 포인트는 100포인트 이상이어야 합니다.");
    }

    @DisplayName("0 미만의 포인트를 사용하려고 하면 예외가 발생한다.")
    @Test
    void useNegativePoint(){
        assertUseThrows(-100, "사용 포인트는 100포인트 이상이어야 합니다.");
    }


    private void assertUseThrows(int amount, String expectedMessage){
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, ()->{
            payment.usePoint(amount, 1000);
        });
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}
