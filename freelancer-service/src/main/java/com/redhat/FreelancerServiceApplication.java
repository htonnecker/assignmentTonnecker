package com.redhat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FreelancerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreelancerServiceApplication.class, args);
    }

}
