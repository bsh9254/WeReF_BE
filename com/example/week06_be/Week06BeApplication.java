package com.example.week06_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Week06BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Week06BeApplication.class, args);
    }

}
