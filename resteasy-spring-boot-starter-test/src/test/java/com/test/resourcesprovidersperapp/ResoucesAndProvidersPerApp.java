package com.test.resourcesprovidersperapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * SpringBoot entry point application
 * <p>
 * Created by facarvalho on 12/7/15.
 */
@SpringBootApplication
public class ResoucesAndProvidersPerApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ResoucesAndProvidersPerApp.class, args);
    }
}
