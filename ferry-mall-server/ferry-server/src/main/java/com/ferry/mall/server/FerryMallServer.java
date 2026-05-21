package com.ferry.mall.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.ferry")
@EnableAsync
public class FerryMallServer {
    public static void main(String[] args) {
        SpringApplication.run(FerryMallServer.class, args);
    }
}
