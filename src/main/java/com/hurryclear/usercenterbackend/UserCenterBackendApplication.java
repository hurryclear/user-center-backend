package com.hurryclear.usercenterbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hurryclear.usercenterbackend.mapper") // this annotation will tell mybatis to scann mapper
public class UserCenterBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserCenterBackendApplication.class, args);
	}

}
