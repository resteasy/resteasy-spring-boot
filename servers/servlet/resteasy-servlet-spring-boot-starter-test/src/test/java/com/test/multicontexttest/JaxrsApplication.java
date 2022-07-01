package com.test.multicontexttest;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS application
 *
 * Created by facarvalho on 12/7/15.
 */
@Component
@ApplicationPath("/sample-app/")
public class JaxrsApplication extends Application {
}
