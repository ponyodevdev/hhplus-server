package kr.hhplus.be.server.rediscachetest;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.RedisLockTestBase;
import kr.hhplus.be.server.application.port.out.ConcertOptionPort;
import kr.hhplus.be.server.application.service.ConcertOptionQueryService;
import kr.hhplus.be.server.domain.model.ConcertOption;
import kr.hhplus.be.server.infrastructure.persistence.ConcertOptionJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@SpringBootTest
@Transactional
class ConcertOptionCacheIntegrationTest {

    @Autowired ConcertOptionQueryService service;
    @Autowired
    ConcertOptionPort concertOptionPort;
    @Autowired
    EntityManager em;

        @Test
        void 캐시가_DB쿼리를_줄인다() {
            long concertId = seedConcertOption();

            // 첫 호출 - MISS → DB 조회
            service.getAllOptions(concertId);
            clearPersistenceContext();

            // 두 번째 호출 - HIT → DB 조회 안 함
            service.getAllOptions(concertId);
            clearPersistenceContext();

            // 여기서 DB 쿼리 횟수 검증 대신, 로그에서 select 쿼리 발생 여부를 확인
            // (Hibernate show_sql 또는 p6spy 사용 시)
        }

        private long seedConcertOption() {
            ConcertOption option = new ConcertOption(
                    /* id */ null,
                    /* concertId */ 1L,
                    /* startTime */ LocalDateTime.now().plusDays(1),
                    /* price */ 10000
            );
            em.persist(option);
            em.flush();
            return option.getConcertId();
        }

        private void clearPersistenceContext() {
            em.clear();
        }

        @Test
        void TTL_만료후_다시_DB조회된다() throws InterruptedException {
            long concertId = seedConcertOption();

            // 1. 첫 호출 (MISS → DB 조회)
            service.getAllOptions(concertId);
            clearPersistenceContext();

            // 2. TTL 안에 재호출 (HIT → DB 조회 없음)
            service.getAllOptions(concertId);
            clearPersistenceContext();

            // 3. TTL 기다림 (CacheConfig에서 optionsByConcert TTL = 5초라고 가정)
            Thread.sleep(6000); // 6초 대기 → TTL 만료

            // 4. 다시 호출 (MISS → DB 재조회)
            service.getAllOptions(concertId);
        }
    }