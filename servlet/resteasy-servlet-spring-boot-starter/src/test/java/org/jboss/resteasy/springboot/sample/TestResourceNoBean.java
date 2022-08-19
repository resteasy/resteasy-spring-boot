package org.jboss.resteasy.springboot.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("resourceNoBean")
public class TestResourceNoBean {

    @GET
    public void get() {
        // Test get method
    }

}
