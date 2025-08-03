package semigg.semi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "semigg.semi.repository")
@EntityScan(basePackages = "semigg.semi.domain")
public class SemiApplication {
	public static void main(String[] args) {
		SpringApplication.run(SemiApplication.class, args);
	}
}