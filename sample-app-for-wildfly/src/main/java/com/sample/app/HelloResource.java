package com.sample.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Component
@Path("/hello")
public class HelloResource {

    @Autowired
    EchoBean bean;

    @GET
    public String get() {
        return bean.echo("Hello, world!");
    }
}
