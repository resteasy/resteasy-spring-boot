package org.jboss.resteasy.springboot.common.sample.resources;

/**
 * This bean creates {@link EchoMessage} objects based on
 * echo texts received as input
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public interface IEchoMessageCreator {

    default public EchoMessage createEchoMessage(final String echoText) {
        return new EchoMessage(echoText);
    }

}
