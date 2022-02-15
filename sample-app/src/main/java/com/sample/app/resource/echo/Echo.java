package com.sample.app.resource.echo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Echo REST endpoint class
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Path("/echo")
@Component
public class Echo {

    @Autowired
    private EchoMessageCreator echoer;

    /**
     * Receives a simple POST request message containing as payload
     * a text, in text plain format, to be echoed by the service.
     * It returns as response, in JSON, the text to be echoed plus a timestamp of the
     * moment the echo response was created on the server side
     *
     * @param echoText
     * @return
     */
    @POST
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON})
    public EchoMessage echo(@NotEmpty(message = "must not be empty") String echoText) {
        return echoer.createEchoMessage(echoText);
    }
}
