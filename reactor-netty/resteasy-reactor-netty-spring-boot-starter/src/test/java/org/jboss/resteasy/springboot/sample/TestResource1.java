package org.jboss.resteasy.springboot.sample;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("resource1")
@Component
public class TestResource1 {

    @GET
    public void get() {
        // Test get method
    }

}
