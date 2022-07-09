package org.jboss.resteasy.springboot.sample;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TestProviderNoBean implements ExceptionMapper<Exception> {

    public Response toResponse(Exception exception) {
        return null;
    }
}
