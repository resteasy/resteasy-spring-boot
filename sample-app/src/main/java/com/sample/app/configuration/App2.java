package com.sample.app.configuration;

import com.sample.app.resource.echo.Echo;
import com.sample.app.resource.echo.Echo2;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@Component
@ApplicationPath("/sample-app2/")
public class App2 extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(Echo2.class);
        return set;
    }
}
