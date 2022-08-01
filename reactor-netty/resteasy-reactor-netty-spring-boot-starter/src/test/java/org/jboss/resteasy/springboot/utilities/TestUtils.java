package org.jboss.resteasy.springboot.utilities;

import java.util.Optional;
import org.jboss.resteasy.springboot.reactor.ReactorNettyServerConfig;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class TestUtils {

    /**
     * Creates an application context from a context file and customizes it with passed properties.
     * @param contextFileLocation - The xml context file to use for creating the application context.
     * @param customPropertySource - Properties to apply before loading the context.
     * @param maybeServerConfig - An optional ReactorNettyServerConfig to use.
     * @return - The created applicationContext.
     */
    public static ApplicationContext configureAndCreateAppContext(final String contextFileLocation,
            final CustomPropertySource customPropertySource, Optional<ReactorNettyServerConfig> maybeServerConfig) {

        final DefaultListableBeanFactory parentBeanFactory = new DefaultListableBeanFactory();
        final GenericApplicationContext parentContext = new GenericApplicationContext(parentBeanFactory);
        maybeServerConfig.ifPresent(config -> {
            parentBeanFactory.registerSingleton("reactorNettyServerConfig", config);
        });
        parentContext.refresh();
    
        final AbstractApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { contextFileLocation },
                false, parentContext);
        appContext.getEnvironment().getPropertySources().addLast(customPropertySource);
        appContext.refresh();

        appContext.registerShutdownHook();
        return appContext;
    }
    
}
