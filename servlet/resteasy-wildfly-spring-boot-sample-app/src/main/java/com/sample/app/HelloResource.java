package com.sample.app;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Component
@Path("/hello")
public class HelloResource {

    @Autowired
    EchoBean bean;

    @Operation
    @ApiResponse(description = "return hello world")
    @GET
    public String get() {
        return bean.echo("Hello, world!");
    }
}
