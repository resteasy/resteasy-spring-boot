package org.jboss.resteasy.springboot.utilities;

import org.springframework.core.env.PropertySource;

public class CustomPropertySource extends PropertySource<String> {

    private static final String JAXRS_APP_CLASSES_PROPERTY = "resteasy.jaxrs.app.classes";
    private static final String JAXRS_APP_CLASSES_DEFINITION_PROPERTY = "resteasy.jaxrs.app.registration";
    private static final String JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY = "resteasy.async.job.service.enabled";
    private static final String REACTOR_NETTY_SERVER_PORT_PROPERTY = "server.port";
    
    private String appClasses;
    private String appClassesDefinition;
    private int port = 0;
    private boolean isAsyncEnabled = false;
    
    public CustomPropertySource() {
        super("custom");
    }

    @Override
    public String getProperty(String name) {
        
        String value = null;
        
        switch (name) {
        case JAXRS_APP_CLASSES_DEFINITION_PROPERTY:
            value = appClassesDefinition;
            break;
        case JAXRS_APP_CLASSES_PROPERTY:
            value = appClasses;
            break;
        case REACTOR_NETTY_SERVER_PORT_PROPERTY:
            value = String.valueOf(port);
            break;
        case JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY:
            value = String.valueOf(isAsyncEnabled);
            break;
            default:
                break;
        }

        return value;
        
    }
    
    public CustomPropertySource setReactorNettyPort(int port) {
        this.port = port;
        return this;
    }
    
    public CustomPropertySource setAppClassesDefinition(String appClassesDefinition) {
        this.appClassesDefinition = appClassesDefinition;
        return this;
    }
    
    public CustomPropertySource setIsAsyncEnabled(boolean isAsyncEnabled) {
        this.isAsyncEnabled = isAsyncEnabled;
        return this;
    }
    
    public CustomPropertySource setAppClasses(String appClasses) {
        this.appClasses = appClasses;
        return this;
    }
    
}
