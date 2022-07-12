package org.jboss.resteasy.springboot.sample;

import jakarta.ws.rs.core.Application;

/**
 * This application, although extending Application class,
 * is NOT annotated with ApplicationPath annotation, which
 * should prevent its registration
 */
public class TestApplication3 extends Application {}
