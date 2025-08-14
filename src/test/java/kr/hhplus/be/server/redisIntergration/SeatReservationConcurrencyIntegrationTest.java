package kr.hhplus.be.server.redisIntergration;

import kr.hhplus.be.server.RedisLockTestBase;
import kr.hhplus.be.server.application.port.in.SeatUseCase;
import kr.hhplus.be.server.domain.model.Seat;
import kr.hhplus.be.server.infrastructure.persistence.SeatJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = "spring.aop.proxy-target-class=true")
class SeatReservationConcurrencyIntegrationTest extends RedisLockTestBase
{

    @Autowired
    SeatUseCase seatUseCase;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @BeforeEach
    void seed() {
        seatJpaRepository.deleteAll();
        seatJpaRepository.save(new Seat(
                /* seatId   */ 17L,
                /* optionId */ null,
                /* seatLabel*/ "A-1"
        ));
    }

    @Test
    void 같은좌석_동시_예약_성공은_1건() throws Exception {
        Long seatId = 17L;
        UUID user = UUID.randomUUID();

        int threads = 50;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            results.add(es.submit(() -> {
                ready.countDown();
                start.await();
                try {
                    seatUseCase.assignSeat(seatId, user);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }));
        }

        ready.await();
        start.countDown();

        int success = 0;
        for (Future<Boolean> f : results) if (f.get()) success++;
        es.shutdown();

        assertThat(success).isEqualTo(1);
    }
}
