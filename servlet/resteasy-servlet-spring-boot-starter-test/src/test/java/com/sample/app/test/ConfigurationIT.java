package com.sample.app.test;

import io.restassured.response.Response;

import org.jboss.resteasy.springboot.common.utils.SocketUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.sample.app.NonSpringBeanJaxrsApplication2;

import jakarta.ws.rs.core.MediaType;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Integration test that tests a couple sample application (see sample-app and sample-app-no-jaxrs-application).
 * This class test possible configurations to register JAX-RS application classes.
 *
 * @author facarvalho
 */
public class ConfigurationIT {

    private CtxAndPort configureAndStartApp(Properties properties, Class springBootApplicationClass) {
        return configureAndStartApp(properties, true, springBootApplicationClass);
    }

    CtxAndPort configureAndStartApp(Properties properties, boolean assertPerfectLog,
                                            Class springBootApplicationClass) {

        final SpringApplicationBuilder builder = new SpringApplicationBuilder(springBootApplicationClass);

        builder.web(WebApplicationType.SERVLET);
        if (assertPerfectLog) {
            builder.listeners(new LogbackTestApplicationListener());
        }
        if (properties != null) {
            builder.properties(properties);
        }

        final int port = SocketUtils.findAvailableTcpPort();

        // give the SpringApplicationBuilder some time to throw exception firstly.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // NOTE: this async operation causes random `invalidClassTest` test failure.
        // InvalidClassTest -> java.lang.AssertionError: Application run failed
        // Which comes from: spring-boot-project/spring-boot/src/main/java/org/springframework/boot/SpringApplication.java:818:			logger.error("Application run failed", failure);
        final ConfigurableApplicationContext ctx = builder.run("--debug", "--server.port=" + port);
        ctx.registerShutdownHook();

        return new CtxAndPort(ctx, port);
    }

    private void assertResourceFound(int port, String basePath) {
        Response response = given().basePath(basePath).port(port).body("is there anybody out there?").contentType(MediaType.TEXT_PLAIN).post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    private void assertResourceNotFound(int port, String basePath) {
        Response response = given().basePath(basePath).port(port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(404).body("status", equalTo(404)).body("error", equalTo("Not Found"));
    }

    @Test
    public void implicitAutoTest() {
        final CtxAndPort ctxAndPort = configureAndStartApp(null, com.sample.app.Application.class);

        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceNotFound(ctxAndPort.port, "sample-app-test");
        assertResourceNotFound(ctxAndPort.port, "/");

        shutdownCtx(ctxAndPort);
    }

    @Test
    public void explicitAutoTest() {
        final Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "auto");

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceNotFound(ctxAndPort.port, "sample-app-test");
        assertResourceNotFound(ctxAndPort.port, "/");

        shutdownCtx(ctxAndPort);
    }

    private void shutdownCtx(CtxAndPort ctxAndPort) {
        Response response = given().port(ctxAndPort.port).basePath("/").contentType("application/json").post("/actuator/shutdown");
        response.then().statusCode(200).body("message", equalTo("Shutting down, bye..."));
    }

    @Test
    public void beansTest() {
        final Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "beans");

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceNotFound(ctxAndPort.port, "sample-app-test");
        assertResourceNotFound(ctxAndPort.port, "/");

        shutdownCtx(ctxAndPort);
    }

    @Test
    public void propertySpringBeanClassTest() {
        final Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", "com.sample.app.configuration.JaxrsApplication");

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceNotFound(ctxAndPort.port, "sample-app-test");
        assertResourceNotFound(ctxAndPort.port, "/");

        shutdownCtx(ctxAndPort);
    }

    @Test
    public void propertyNonSpringBeanClassTest() {
        final Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", NonSpringBeanJaxrsApplication2.class.getTypeName());

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        assertResourceNotFound(ctxAndPort.port, "sample-app");
        assertResourceFound(ctxAndPort.port, "sample-app-test");
        assertResourceNotFound(ctxAndPort.port, "/");

        shutdownCtx(ctxAndPort);
    }



    @Test
    public void scanningTest() {
        final Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "scanning");

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        // we expect that the scanning will only find jax-rs application classes that are located
        // under the package that the main Spring Boot application class is found
        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceFound(ctxAndPort.port, "sample-app-test-two");
        assertResourceNotFound(ctxAndPort.port, "/");

        shutdownCtx(ctxAndPort);
    }

    @Test
    public void noJaxrsApplicationAndImplicitAutoTest() {
        final CtxAndPort ctxAndPort = configureAndStartApp(null, com.sample.app2.Application.class);

        // since there is no jax-rs application class, we expect the app to respond on the root path
        assertResourceFound(ctxAndPort.port, "/");

        // this test is special so we manually shutdown the server here
        ctxAndPort.ctx.stop();
    }

    @Test
    public void noJaxrsApplicationAndImplicitAutoWithDefaultPathTest() {
        final Properties properties = new Properties();
        properties.put("resteasy.jaxrs.defaultPath", "/testpath");
        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app2.Application.class);

        // since there is no jax-rs application class, we expect the app to respond on the root path
        assertResourceFound(ctxAndPort.port, "/testpath");

        shutdownCtx(ctxAndPort);
    }

    private static class CtxAndPort {
        final ConfigurableApplicationContext ctx;
        final int port;


        CtxAndPort(ConfigurableApplicationContext ctx, int port) {
            this.port = port;
            this.ctx = ctx;
        }
    }
}
