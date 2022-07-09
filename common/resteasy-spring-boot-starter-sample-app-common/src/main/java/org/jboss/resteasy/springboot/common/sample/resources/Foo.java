package org.jboss.resteasy.springboot.common.sample.resources;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Component
@Path("foo")
public class Foo {
  
    private  Random random;
    
    @Inject
    private IEchoMessageCreator echoer;
    
  
    public Foo(Random random) {
        this.random = random;
    }
    
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public EchoMessage echoRandom() {
        return echoer.createEchoMessage(Integer.toString(random.random));
    }
    
}

class Random {
    public int random = new java.util.Random().nextInt();
}

@Configuration
class Config {
    @Bean
    public static Random mkRandom() {
        return new Random();
    }
}
