package com.sample.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.ws.rs.ApplicationPath;

/**
 * SpringBoot entry point application
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@SpringBootApplication
@ApplicationPath("/rest")
public class MyApp extends javax.ws.rs.core.Application {

    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }

}
