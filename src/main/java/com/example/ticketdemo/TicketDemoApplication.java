package com.example.ticketdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.ticketdemo.mapper")
public class TicketDemoApplication {


	public static void main(String[] args) {
        SpringApplication.run(TicketDemoApplication.class, args);
	}

}
