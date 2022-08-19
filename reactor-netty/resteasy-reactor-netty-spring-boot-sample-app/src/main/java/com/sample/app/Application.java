package com.sample.app;

import org.jboss.resteasy.springboot.reactor.ReactorNettyServerConfig;
import org.jboss.resteasy.springboot.common.sample.configuration.SampleSSLContextFactory;
import org.jboss.resteasy.springboot.common.sample.resources.IEchoMessageCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.netty.handler.ssl.ClientAuth;
/**
 * SpringBoot entry point application
 */
@SpringBootApplication(scanBasePackages = { "com.sample.app", "org.jboss.resteasy.springboot"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public ReactorNettyServerConfig reactorNettyServerConfig() {
        return new ReactorNettyServerConfig.Builder()
                .withSSLContext(SampleSSLContextFactory
                		.sslContext("test.keystore", "test123"))
                .withClientAuth(ClientAuth.NONE)
                .build();
    }
    
    @Bean
    public IEchoMessageCreator echoMessageCreator() {
    	return new IEchoMessageCreator() {};
    }
    
}
