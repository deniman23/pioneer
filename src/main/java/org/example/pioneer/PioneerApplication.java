package org.example.pioneer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PioneerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PioneerApplication.class, args);
    }
}