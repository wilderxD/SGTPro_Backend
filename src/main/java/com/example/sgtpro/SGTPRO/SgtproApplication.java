package com.example.sgtpro.SGTPRO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SgtproApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgtproApplication.class, args);
    }

}
