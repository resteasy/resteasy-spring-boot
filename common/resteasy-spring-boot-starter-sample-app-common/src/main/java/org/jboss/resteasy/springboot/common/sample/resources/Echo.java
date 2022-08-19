package org.jboss.resteasy.springboot.common.sample.resources;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Echo REST endpoint class
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Path("/echo")
@Component
public class Echo {

    @Autowired
    private IEchoMessageCreator echoer;

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
