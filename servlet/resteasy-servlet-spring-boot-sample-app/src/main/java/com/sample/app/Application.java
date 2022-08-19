package com.sample.app;

import org.jboss.resteasy.springboot.common.sample.resources.IEchoMessageCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * SpringBoot entry point application
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@SpringBootApplication(scanBasePackages = { "com.sample.app", "org.jboss.resteasy.springboot"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public IEchoMessageCreator echoMessageCreator() {
        return new IEchoMessageCreator() {};
    }

}
