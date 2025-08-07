package kr.hhplus.be.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "kr.hhplus.be.server")
@EntityScan(basePackages = "kr.hhplus.be.server.domain.model")
@EnableJpaRepositories(basePackages = "kr.hhplus.be.server.infrastructure.persistence")
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
