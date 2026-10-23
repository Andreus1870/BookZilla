package com.bookzilla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookZillaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookZillaApplication.class, args);
    }
}