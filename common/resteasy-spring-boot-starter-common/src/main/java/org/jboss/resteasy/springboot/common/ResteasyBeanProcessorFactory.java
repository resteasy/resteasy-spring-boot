package org.jboss.resteasy.springboot.common;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class that creates a spring bean processor for Resteasy. See
 * {@link org.jboss.resteasy.plugins.spring.SpringBeanProcessor}.
 */
public class ResteasyBeanProcessorFactory {

    private static final Logger logger = LoggerFactory.getLogger(ResteasyBeanProcessorFactory.class);

    public static SpringBeanProcessor resteasySpringBeanProcessor() {
        ResteasyProviderFactory resteasyProviderFactory = new ResteasyProviderFactoryImpl();
        ResourceMethodRegistry resourceMethodRegistry = new ResourceMethodRegistry(resteasyProviderFactory);

        SpringBeanProcessor resteasySpringBeanProcessor = new SpringBeanProcessor();
        resteasySpringBeanProcessor.setProviderFactory(resteasyProviderFactory);
        resteasySpringBeanProcessor.setRegistry(resourceMethodRegistry);

        logger.debug("Resteasy Spring Bean Processor has been created");

        return resteasySpringBeanProcessor;
    }

}
