package kr.hhplus.be.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class QueueConfig {

    @Bean
    public Duration tokenTTL() {
        return Duration.ofMinutes(5);  // 원하는 TTL 설정
    }
}