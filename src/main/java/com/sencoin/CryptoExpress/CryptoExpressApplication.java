package com.sencoin.CryptoExpress;

import com.sencoin.CryptoExpress.Security.CorsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.sencoin.CryptoExpress")

@EntityScan(basePackages = "com.sencoin.CryptoExpress.Entities")
@EnableJpaRepositories(basePackages = "com.sencoin.CryptoExpress.Repository")
@Import(CorsConfig.class)
public class CryptoExpressApplication {


	public static void main(String[] args) {
		SpringApplication.run(CryptoExpressApplication.class, args);
	}

}
