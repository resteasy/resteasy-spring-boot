package org.jboss.resteasy.springboot.reactor;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyJaxrsServer;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.springboot.common.DeploymentCustomizer;
import org.jboss.resteasy.springboot.common.ResteasyBeanProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;


/**
 * This is the main class that prepares a Resteasy deployment and starts a Reactor Netty server.
 */
@Configuration
@EnableConfigurationProperties
public class ResteasyAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ResteasyAutoConfiguration.class);

    private static final String JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY = "resteasy.async.job.service.enabled";
    private static final String JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY_DEFAULT = "false";
    private static final String DEFAULT_BASE_APP_PATH = "/";
    
    @Bean
    public static ResteasyBeanProcessorReactorNetty resteasyBeanProcessorReactorNetty() {
        return new ResteasyBeanProcessorReactorNetty();
    }
    
    @Bean
    public static BeanFactoryPostProcessor resteasySpringBeanProcessor() {
        return ResteasyBeanProcessorFactory.resteasySpringBeanProcessor();
    }
    
    @Bean
    public ResteasyReactorNettyServerBean resteasyReactorNettyServerBean(
            final BeanFactoryPostProcessor resteasySpringBeanProcessor,
            final ResteasyBeanProcessorReactorNetty resteasyBeansProcessor,
            final Environment environment,
            final Optional<ReactorNettyServerConfig> maybeServerConfig) throws InterruptedException {
        
        final ReactorNettyJaxrsServer server =  new ReactorNettyJaxrsServer();
        server.setDeployment(new ResteasyDeploymentImpl());

        final ReactorNettyServerConfig serverConfig = maybeServerConfig
                .orElseGet(() -> ReactorNettyServerConfig.defaultConfig(environment));
        
        configureServerAndDeployment(server, resteasyBeansProcessor.getApplications(),
                (SpringBeanProcessor) resteasySpringBeanProcessor, environment,
                serverConfig);

        server.getDeployment().start();

        return new ResteasyReactorNettyServerBean(server);
    }

    
    public class ResteasyReactorNettyServerBean {
        
        private final ReactorNettyJaxrsServer server;
        private final CountDownLatch shutdownLatch = new CountDownLatch(1);
        
        public ResteasyReactorNettyServerBean(ReactorNettyJaxrsServer server) throws InterruptedException {
            this.server = server;
        }
        
        /**
         * Starts the server a non daemon thread to prevent it from shutting down prematurely.<br>
         * Similar to:
         * https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/web/embedded/netty/NettyWebServer.java
         *
         * @param server - The Reactor Netty server to start.
         * @param shutdownLatch - The countdown latch to use in order to wait for the server to shutdown.
         * @throws InterruptedException - If thread is interrupted while waiting for the server to start.
         */
        @PostConstruct
        private void startServer() throws InterruptedException {
            final CountDownLatch startupLatch = new CountDownLatch(1);
            final Thread awaitThread = new Thread("server") {
                @Override
                public void run() {
                    server.start();
                    logger.info("Reactor Netty server started on port: {}", server.getPort());
                    startupLatch.countDown();
                    try {
                        shutdownLatch.await();
                    } catch (final InterruptedException ie) {
                        logger.error("Exception caught while waiting for the Reactor Netty server to stop", ie);
                        Thread.currentThread().interrupt();
                    }
                }
            };
            awaitThread.setContextClassLoader(MethodHandles.lookup().lookupClass().getClassLoader());
            awaitThread.setDaemon(false);
            awaitThread.start();
            startupLatch.await(); 
        }
        
        @PreDestroy
        private void stopServer() {
            logger.info("Stopping the JAX-RS+Reactor-Netty server.");
            server.stop();
            shutdownLatch.countDown();
        }
        
        public ReactorNettyJaxrsServer getServer() {
            return server;
        }
        
    }

    
    private void configureServerAndDeployment(
            final ReactorNettyJaxrsServer server, 
            final Set<Class<? extends Application>> applications, 
            final SpringBeanProcessor resteasySpringBeanProcessor, 
            final Environment env,
            final ReactorNettyServerConfig config) {

        final ResteasyDeployment deployment = server.getDeployment();
                
        if (applications.isEmpty()) {
            logger.info(
                    "No JAX-RS Application classes with proper path have been found. A default app mapped to '{}', will be configured.",
                    DEFAULT_BASE_APP_PATH);
            deployment.setApplicationClass(Application.class.getName());
        } else {

            final Class<? extends Application> application = (Class<? extends Application>) applications.iterator().next();
            final String path = AnnotationUtils.findAnnotation(application, ApplicationPath.class).value();

            if (applications.size() > 1) {
                logger.info(
                        "Multiple application classes found. Will only configure one. Application name: {}, Base path: {}",
                        application.getName(), path);
            }

            logger.info("Configuring JAX-RS application class {}. Base path: {}", application.getName(), path);
            deployment.setApplicationClass(application.getName());
        }

        final boolean enableAsyncJob = Boolean.valueOf(env.getProperty(JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY,
                JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY_DEFAULT));
        
        DeploymentCustomizer.customizeRestEasyDeployment(resteasySpringBeanProcessor, deployment, enableAsyncJob);
 
        Optional.ofNullable(config.getSslContext())
                .map(c -> server.setSSLContext(c))
                .orElse(server)
                .setPort(config.getPort())
                .setIdleTimeout(config.getIdleTimeout())
                .setClientAuth(config.getClientAuth())
                .setSecurityDomain(config.getSecurityDomain())
                .setCleanUpTasks(config.getCleanupTasks())
                .setDeployment(deployment);

    }


}
