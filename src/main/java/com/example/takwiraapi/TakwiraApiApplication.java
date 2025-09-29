package com.example.takwiraapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TakwiraApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TakwiraApiApplication.class, args);
    }

}
