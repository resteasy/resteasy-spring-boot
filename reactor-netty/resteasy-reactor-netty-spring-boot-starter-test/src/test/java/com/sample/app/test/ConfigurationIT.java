package com.sample.app.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.testng.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.jboss.resteasy.springboot.reactor.ResteasyAutoConfiguration.ResteasyReactorNettyServerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.sample.app.NonSpringBeanJaxrsApplication2;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Integration test that tests a couple sample application (see sample-app and sample-app-no-jaxrs-application).
 * This class test possible configurations to register JAX-RS application classes.
 */
public class ConfigurationIT {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationIT.class);

    private static final String REACTOR_NETTY_SERVER_PORT_PROPERTY = "server.port";
    private static final String JAXRS_APP_CLASSES_DEFINITION_PROPERTY = "resteasy.jaxrs.app.registration";
    private static final String JAXRS_APP_CLASSES_PROPERTY = "resteasy.jaxrs.app.classes";
    private enum JaxrsAppClassesRegistration {
        BEANS, PROPERTY, SCANNING, AUTO
    }
    
    private CtxAndPort configureAndStartApp(Properties properties, Class springBootApplicationClass) {
        return configureAndStartApp(properties, true, springBootApplicationClass);
    }

    private CtxAndPort configureAndStartApp(Properties properties, boolean assertPerfectLog,
                                            Class springBootApplicationClass){
        
        // Start app
        final SpringApplicationBuilder builder = new SpringApplicationBuilder(springBootApplicationClass);
        if (assertPerfectLog) {
            builder.listeners(new LogbackTestApplicationListener());
        }
        builder.properties(properties);
        final ConfigurableApplicationContext appContext = builder.run("--" + REACTOR_NETTY_SERVER_PORT_PROPERTY + "=" + 0);
        appContext.registerShutdownHook();
        
        // Find running port
        ResteasyReactorNettyServerBean serverBean = appContext.getBean(ResteasyReactorNettyServerBean.class);
        
        
        int appPort = 0;
        int maxWaitInSec = 5;
        
        while(appPort == 0 && maxWaitInSec > 0) {
            try {
                Thread.sleep(1000);//Allow for server to start
            } catch (InterruptedException e) {
                logger.error("InterruptedException caught while waiting for the server to start", e);
                Assert.fail("InterruptedException caught while waiting for the server to start");
            }
            maxWaitInSec--;
            appPort = serverBean.getServer().getPort();
        }    
        if(appPort == 0) {
            Assert.fail("Coudn't find server configured port within " + maxWaitInSec + " seconds.");
        }

        RestAssured.baseURI = "https://localhost";
        RestAssured.useRelaxedHTTPSValidation();
        
        return new CtxAndPort(appContext, appPort);
    }

    private void assertResourceFound(int port, String basePath) {
        final Response response = given().basePath(basePath).port(port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    private void assertResourceNotFound(int port, String basePath) {
        final Response response = given().basePath(basePath).port(port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(404).statusLine("HTTP/1.1 404 Not Found");
    }

    private void assertOnlyOneFound(int port, String basePath1, String basePath2, String basePath3) {
        
        final List<Response> responses = new ArrayList<>();
        responses.add(given().basePath(basePath1).port(port).body("is there anybody out there?").post("/echo"));
        responses.add(given().basePath(basePath2).port(port).body("is there anybody out there?").post("/echo"));
        responses.add(given().basePath(basePath3).port(port).body("is there anybody out there?").post("/echo"));
        
        int okCount = 0;
        int notFoundCount = 0;
        
        for(Response response : responses) {
            if(200 == response.getStatusCode()) {
                okCount++;
            }
            else if(404 == response.getStatusCode()) {
                notFoundCount++;
            }
        }
        
        assertEquals(okCount, 1);
        assertEquals(notFoundCount, 2);
    }
    
    @Test
    public void implicitAutoTest() {
        final CtxAndPort ctxAndPort = configureAndStartApp(new Properties() , com.sample.app.Application.class);

        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceNotFound(ctxAndPort.port, "sample-app-test");
        assertResourceFound(ctxAndPort.port, "/"); //TODO Not found

        ctxAndPort.ctx.close();
    }

    @Test
    public void explicitAutoTest() {
        final Properties properties = new Properties();
        properties.put(JAXRS_APP_CLASSES_DEFINITION_PROPERTY, JaxrsAppClassesRegistration.AUTO);

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceFound(ctxAndPort.port, "/");//TODO Not found
        assertResourceNotFound(ctxAndPort.port, "sample-app-test");
        
        ctxAndPort.ctx.close();
    }

    @Test
    public void beansTest() {
        final Properties properties = new Properties();
        properties.put(JAXRS_APP_CLASSES_DEFINITION_PROPERTY, JaxrsAppClassesRegistration.BEANS);

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceNotFound(ctxAndPort.port, "sample-app-test");
        assertResourceFound(ctxAndPort.port, "/"); //TODO Not found

        ctxAndPort.ctx.close();
    }

    @Test
    public void propertySpringBeanClassTest() {
        final Properties properties = new Properties();
        properties.put(JAXRS_APP_CLASSES_DEFINITION_PROPERTY, JaxrsAppClassesRegistration.PROPERTY);
        properties.put(JAXRS_APP_CLASSES_PROPERTY, "com.sample.app.configuration.JaxrsApplication");

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        assertResourceFound(ctxAndPort.port, "sample-app");
        assertResourceNotFound(ctxAndPort.port, "sample-app-test");
        assertResourceFound(ctxAndPort.port, "/"); //TODO Not found

        ctxAndPort.ctx.close();
    }

    @Test
    public void propertyNonSpringBeanClassTest() {
        final Properties properties = new Properties();
        properties.put(JAXRS_APP_CLASSES_DEFINITION_PROPERTY, JaxrsAppClassesRegistration.PROPERTY);
        properties.put(JAXRS_APP_CLASSES_PROPERTY, NonSpringBeanJaxrsApplication2.class.getTypeName());

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        assertOnlyOneFound(ctxAndPort.port, "sample-app", "sample-app-test", "sample-app-test-two");
        assertResourceFound(ctxAndPort.port, "/"); //TODO Not found

        ctxAndPort.ctx.close();
    }

    @Test
    public void invalidClassTest() {
        final Properties properties = new Properties();
        properties.put(JAXRS_APP_CLASSES_DEFINITION_PROPERTY, JaxrsAppClassesRegistration.PROPERTY);
        properties.put(JAXRS_APP_CLASSES_PROPERTY, "com.foor.bar.NonExistentApplicationClass");

        try {
            configureAndStartApp(properties, false, com.sample.app.Application.class);

            Assert.fail("Expected exception, due to class not found, has not been thrown");
        } catch (BeansException ex) {
            Assert.assertEquals(ex.getCause().getClass(), ClassNotFoundException.class);
            Assert.assertEquals(ex.getCause().getMessage(), "com.foor.bar.NonExistentApplicationClass");
        }
    }

    @Test
    public void scanningTest() {
        final Properties properties = new Properties();
        properties.put(JAXRS_APP_CLASSES_DEFINITION_PROPERTY, JaxrsAppClassesRegistration.SCANNING);

        final CtxAndPort ctxAndPort = configureAndStartApp(properties, com.sample.app.Application.class);

        // we expect that the scanning will only find one of the jax-rs application classes that are located
        // under the package that the main Spring Boot application class is found
        assertOnlyOneFound(ctxAndPort.port, "sample-app", "sample-app-test", "sample-app-test-two");
        assertResourceFound(ctxAndPort.port, "/");//TO Not found

        ctxAndPort.ctx.close();
    }
  
    @Test
    public void noJaxrsApplicationAndImplicitAutoTest() {
        final CtxAndPort ctxAndPort = configureAndStartApp(new Properties(), com.sample.app2.Application.class);
        RestAssured.baseURI = "http://localhost";
        // since there is no jax-rs application class, we expect the app to respond on the root path
        assertResourceFound(ctxAndPort.port, "/");

        ctxAndPort.ctx.close();
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
