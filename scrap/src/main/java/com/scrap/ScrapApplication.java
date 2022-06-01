package com.scrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;


@SpringBootApplication(scanBasePackages = {"com.scrap", "com.scrap.scrap"}, scanBasePackageClasses = KafkaTemplate.class)
public class ScrapApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapApplication.class, args);
    }
}
