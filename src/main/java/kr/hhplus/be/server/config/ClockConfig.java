package kr.hhplus.be.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    @Bean
    public Clock systemDefaultZoneClock() {
        return Clock.systemDefaultZone(); // or systemUTC()
    }
}