package org.jboss.resteasy.springboot.sample;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Component
@Provider
public class TestProvider1 implements ExceptionMapper<Exception> {

    public Response toResponse(Exception exception) {
        return null;
    }
}
