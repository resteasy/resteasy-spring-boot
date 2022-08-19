package org.jboss.resteasy.springboot.sample;

import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class TestProvider1 implements ExceptionMapper<Exception> {

    public Response toResponse(Exception exception) {
        return null;
    }
}
