package com.sample.app.configuration;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS application
 */
@Component
@ApplicationPath("/sample-app/")
public class JaxrsApplication extends Application {}
