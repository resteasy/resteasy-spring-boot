package com.test.resourcesprovidersperapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
//import org.springframework.boot.web.support.SpringBootServletInitializer; // pre 2.0 ref

/**
 * SpringBoot entry point application
 *
 * Created by facarvalho on 12/7/15.
 */
@SpringBootApplication
public class ResoucesAndProvidersPerApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ResoucesAndProvidersPerApp.class, args);
    }

}
