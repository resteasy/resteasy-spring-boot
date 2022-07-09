package org.jboss.resteasy.springboot.common;

import java.util.Objects;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * The main function of this class is to prepare, configure and initialize the core components of a RESTEasy deployment.
 */
public class DeploymentCustomizer {
    
    /**
     * Configures and initializes a resteasy deployment with:<br>
     * - A {@code org.jboss.resteasy.spi.Dispatcher}<br>
     * - A {@code org.jboss.resteasy.spi.ResteasyProviderFactory}<br>
     * - A {@code org.jboss.resteasy.core.ResourceMethodRegistry}
     * 
     * @param resteasySpringBeanProcessor
     *            - The spring bean processor to acquire the provider and resource factories from.
     * @param deployment
     *            - The deployment to customize.
     * @param enableAsyncJob
     *            - Indicates whether the async job service should be enabled.
     */
    public static void customizeRestEasyDeployment(SpringBeanProcessor resteasySpringBeanProcessor, ResteasyDeployment deployment, boolean enableAsyncJob) {
       
        Objects.requireNonNull(resteasySpringBeanProcessor);
        Objects.requireNonNull(deployment);
        
        final ResteasyProviderFactory resteasyProviderFactory = resteasySpringBeanProcessor.getProviderFactory();
        final ResourceMethodRegistry resourceMethodRegistry = (ResourceMethodRegistry) resteasySpringBeanProcessor.getRegistry();
        
        deployment.setProviderFactory(resteasyProviderFactory);
        deployment.setRegistry(resourceMethodRegistry);

        if(enableAsyncJob) {
            deployment.setAsyncJobServiceEnabled(true);
            final AsynchronousDispatcher dispatcher = new AsynchronousDispatcher(resteasyProviderFactory, resourceMethodRegistry);
            deployment.setDispatcher(dispatcher);
        } else {
            final SynchronousDispatcher dispatcher = new SynchronousDispatcher(resteasyProviderFactory, resourceMethodRegistry);
            deployment.setDispatcher(dispatcher);
        }

    }

}
