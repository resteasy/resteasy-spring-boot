package com.sample.app;

//import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Component
@Path("/hello")
public class HelloResource {

    @Autowired
    EchoBean bean;

//    @APIResponse(description = "return hello world")
    @GET
    public String get() {
        return bean.echo("Hello, world!");
    }
}
