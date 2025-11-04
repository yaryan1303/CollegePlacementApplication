package com.college.PlacementApl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PlacementAplApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlacementAplApplication.class, args);
    }

}
