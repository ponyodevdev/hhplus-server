package kr.hhplus.be.server.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config; // ★ Redisson 설정 클래스

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        // Redisson 설정
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379");

        return Redisson.create(config);
    }

}