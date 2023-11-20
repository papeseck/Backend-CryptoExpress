package com.sencoin.CryptoExpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication

@EntityScan(basePackages = "com.sencoin.CryptoExpress.Entities")
@EnableJpaRepositories(basePackages = "com.sencoin.CryptoExpress.Repository")
public class CryptoExpressApplication {


	public static void main(String[] args) {
		SpringApplication.run(CryptoExpressApplication.class, args);
	}

}
