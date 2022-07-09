package org.jboss.resteasy.springboot.sample;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * Created by facarvalho on 6/9/16.
 */
@Path("resource2")
@Component
public class TestResource2 {

    @GET
    public void get() {
        // Test get method
    }

}
