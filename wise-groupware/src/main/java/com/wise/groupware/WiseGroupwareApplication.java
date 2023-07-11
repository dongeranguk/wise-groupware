package com.wise.groupware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class WiseGroupwareApplication {

    public static void main(String[] args) {
        SpringApplication.run(WiseGroupwareApplication.class, args);
    }

}
