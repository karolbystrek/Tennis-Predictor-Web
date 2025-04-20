package com.karolbystrek.tennispredictor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TennisPredictorWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TennisPredictorWebApplication.class, args);
    }

}
