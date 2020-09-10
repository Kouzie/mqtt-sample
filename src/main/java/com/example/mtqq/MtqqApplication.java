package com.example.mtqq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MtqqApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtqqApplication.class, args);
    }
}
