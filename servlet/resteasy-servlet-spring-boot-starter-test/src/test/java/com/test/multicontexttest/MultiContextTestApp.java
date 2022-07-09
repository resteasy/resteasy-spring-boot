package com.test.multicontexttest;

import org.jboss.resteasy.springboot.common.sample.resources.EchoMessage;
import org.jboss.resteasy.springboot.common.sample.resources.IEchoMessageCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * SpringBoot entry point application
 *
 * Created by facarvalho on 12/7/15.
 */
@SpringBootApplication(scanBasePackages = { "org.jboss.resteasy.springboot", "com.test.multicontexttest"})
public class MultiContextTestApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MultiContextTestApp.class, args);
    }
    
    @Bean
    public IEchoMessageCreator echoMessageCreator() {
    	return new IEchoMessageCreator() {
    		public EchoMessage createEchoMessage(final String echoText) {
    			return new EchoMessage("I don't want to echo anything today");
    	    }
    	};
    }

}
