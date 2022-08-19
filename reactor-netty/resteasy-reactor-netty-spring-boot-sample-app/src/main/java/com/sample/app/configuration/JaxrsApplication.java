package com.sample.app.configuration;

import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS application
 */
@Component
@ApplicationPath("/sample-app/")
public class JaxrsApplication extends Application {}
