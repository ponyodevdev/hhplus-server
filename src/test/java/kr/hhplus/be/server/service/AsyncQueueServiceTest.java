package kr.hhplus.be.server.service;

import kr.hhplus.be.server.application.service.AsyncQueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class AsyncQueueServiceTest {

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7.2")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    AsyncQueueService queueService;

    @Test
    void 토큰발급과순위확인() {
        String token = queueService.issueToken(100L);
        Long rank = queueService.getRank(100L);

        assertThat(token).isNotBlank();
        assertThat(rank).isEqualTo(1);
    }
}
