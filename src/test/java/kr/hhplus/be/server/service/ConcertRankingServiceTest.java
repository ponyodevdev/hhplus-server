package kr.hhplus.be.server.service;

import kr.hhplus.be.server.application.service.ConcertRankingService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcertRankingServiceTest {

    @Autowired
    private ConcertRankingService service;

    @Test
    void topN랭킹을조회한다() {
        // given
        service.recordSale(1L, false); // concert1 판매
        service.recordSale(2L, false); // concert2 판매
        service.recordSale(3L, true);  // concert3 매진
        service.recordSale(2L, false); // concert2 판매 증가

        List<String> ranking = service.getRanking(5);
        System.out.println(ranking);

        // then
        // 매진 콘서트(concert3) 먼저 → 판매량 순서(concert2, concert1)
        assertThat(ranking).containsExactly("concert3", "concert2", "concert1");
    }
}
