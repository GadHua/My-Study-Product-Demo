package com.gadhub.overseasproduct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.gadhub.overseasproduct.mapper")
@SpringBootApplication
@EnableScheduling
public class OverseasProductApplication {

	public static void main(String[] args) {

		SpringApplication.run(OverseasProductApplication.class, args);
	}

}

//