package org.jboss.resteasy.springboot.sample;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("resourceNoBean")
public class TestResourceNoBean {

    @GET
    public void get() {
        // Test get method
    }

}
