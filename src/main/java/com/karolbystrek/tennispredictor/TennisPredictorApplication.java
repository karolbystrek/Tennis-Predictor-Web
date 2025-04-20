package com.karolbystrek.tennispredictor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TennisPredictorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TennisPredictorApplication.class, args);
    }

}
