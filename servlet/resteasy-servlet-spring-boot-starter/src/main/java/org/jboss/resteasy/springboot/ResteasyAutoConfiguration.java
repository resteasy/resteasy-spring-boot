package org.jboss.resteasy.springboot;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.springboot.common.DeploymentCustomizer;
import org.jboss.resteasy.springboot.common.ResteasyBeanProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is the main class that configures this Resteasy Sring Boot starter
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties
public class ResteasyAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(ResteasyAutoConfiguration.class);

    /**
     * This is a modified version of {@link ResteasyBootstrap}
     * @param resteasySpringBeanProcessor - A bean processor for Resteasy.
     *
     * @return a ServletContextListener object that configures and start a ResteasyDeployment
     */
    @Bean
    public ServletContextListener resteasyBootstrapListener(
            final @Qualifier("resteasySpringBeanProcessor") SpringBeanProcessor resteasySpringBeanProcessor) {

        ServletContextListener servletContextListener = new ServletContextListener() {

            protected ResteasyDeployment deployment;

            public void contextInitialized(ServletContextEvent sce) {
                ServletContext servletContext = sce.getServletContext();

                deployment = new ListenerBootstrap(servletContext).createDeployment();
                DeploymentCustomizer.customizeRestEasyDeployment(resteasySpringBeanProcessor, deployment,
                        deployment.isAsyncJobServiceEnabled());
                deployment.start();

                servletContext.setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
                servletContext.setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
                servletContext.setAttribute(Registry.class.getName(), deployment.getRegistry());
            }

            public void contextDestroyed(ServletContextEvent sce) {
                if (deployment != null) {
                    deployment.stop();
                }
            }
        };

        logger.debug("ServletContextListener has been created");

        return servletContextListener;
    }

    @Bean(name = ResteasyApplicationBuilder.BEAN_NAME)
    public ResteasyApplicationBuilder resteasyApplicationBuilder() {
        return new ResteasyApplicationBuilder();
    }

    @Bean
    public static ResteasyBeanProcessorTomcat resteasyBeanProcessorTomcat() {
        return new ResteasyBeanProcessorTomcat();
    }

    @Bean("resteasySpringBeanProcessor")
    public static SpringBeanProcessor resteasySpringBeanProcessor() {
        return ResteasyBeanProcessorFactory.resteasySpringBeanProcessor();
    }
}
