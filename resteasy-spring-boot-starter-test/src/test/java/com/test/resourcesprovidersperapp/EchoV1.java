package com.test.resourcesprovidersperapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Echo REST endpoint class
 *
 * Created by facarvalho on 12/7/15.
 */
@Path("/echo1")
@Component
public class EchoV1 {

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
    @Consumes({ MediaType.TEXT_PLAIN })
    @Produces({ MediaType.APPLICATION_JSON })
    public EchoMessage echo(String echoText) {
        return new EchoMessage("echoed v1 -> " + echoText);
    }

}
