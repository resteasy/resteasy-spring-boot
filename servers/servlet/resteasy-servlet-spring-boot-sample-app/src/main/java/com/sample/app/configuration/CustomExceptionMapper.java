package com.sample.app.configuration;

import com.sample.app.bean.CustomSingletonBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Custom exception mapper for 404 cases.
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Component
@Provider
public class CustomExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Autowired
    private CustomSingletonBean customSingletonBean;

    public Response toResponse(NotFoundException exception) {

        // This will cause a NPE if this bean couldn't be injected,
        // and that is all we want to check. No need for assertions here
        customSingletonBean.amIAlive();

        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.NOT_FOUND).entity("The resource you've requested, has not been found!");
        responseBuilder.type(MediaType.TEXT_PLAIN);
        return responseBuilder.build();
    }

}
