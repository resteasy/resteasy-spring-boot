package org.jboss.resteasy.springboot.sample;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TestProviderNoBean implements ExceptionMapper<Exception> {

    public Response toResponse(Exception exception) {
        return null;
    }
}
