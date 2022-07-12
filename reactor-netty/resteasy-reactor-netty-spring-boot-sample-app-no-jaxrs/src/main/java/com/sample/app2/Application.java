package com.sample.app2;

import org.jboss.resteasy.springboot.common.sample.resources.IEchoMessageCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"org.jboss.resteasy.springboot" })
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public IEchoMessageCreator echoMessageCreator() {
    	return new IEchoMessageCreator() {};
    }
    
}
