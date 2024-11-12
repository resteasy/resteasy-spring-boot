package com.sample.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * SpringBoot entry point application
 *
 * To deploy to Wildfly, this class must extends SpringBootServletInitializer.
 */
@SpringBootApplication
public class SpringApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
    }

}
