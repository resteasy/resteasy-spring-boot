package com.sample.app.configuration;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

@Component
@Provider
public class RequestFilterFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        FeatureEnabled featureEnabled = resourceInfo.getResourceMethod()
                .getAnnotation(FeatureEnabled.class);
        if (featureEnabled != null) {
            featureContext.register(CustomFeatureRequestFilter.class);
        }
    }
}
