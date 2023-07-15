package com.foolcats;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class FakeBilibiliApplication {
    public static void main(String[] args) {
        ApplicationContext application = SpringApplication.run(FakeBilibiliApplication.class, args);
    }
}
