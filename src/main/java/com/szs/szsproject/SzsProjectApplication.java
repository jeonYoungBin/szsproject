package com.szs.szsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SzsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SzsProjectApplication.class, args);
    }

}
