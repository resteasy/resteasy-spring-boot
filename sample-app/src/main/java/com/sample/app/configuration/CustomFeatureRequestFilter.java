package com.sample.app.configuration;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.app.bean.CustomSingletonBean;

@Component
public class CustomFeatureRequestFilter implements ContainerRequestFilter {

    @Autowired
    private CustomSingletonBean customSingletonBean;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        // This will cause a NPE if this bean couldn't be injected,
        // and that is all we want to check. No need for assertions here
        customSingletonBean.amIAlive();
    }
}
