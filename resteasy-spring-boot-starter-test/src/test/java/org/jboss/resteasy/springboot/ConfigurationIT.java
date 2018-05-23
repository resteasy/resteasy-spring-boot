package org.jboss.resteasy.springboot;

import com.sample.app.Application;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.util.SocketUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * This is an integration test based on a simple sample application (see sample-app project).
 * This class test possible configurations to register JAX-RS application classes.
 *
 * @author facarvalho
 */
public class ConfigurationIT {

    private Properties properties;

    private int configureAndStartApp(Properties properties) {
        return configureAndStartApp(properties, true);
    }

    private int configureAndStartApp(Properties properties, boolean assertPerfectLog) {

        int appPort = SocketUtils.findAvailableTcpPort();

        RestAssured.basePath = "sample-app";
        RestAssured.port = appPort;

        SpringApplication app = new SpringApplication(Application.class);

        if (properties != null) {
            app.setDefaultProperties(properties);
        }

        app.run("--server.port=" + appPort);

        return appPort;
    }

    private void assertResourceFound(int port, String basePath) {
        Response response = given().basePath(basePath).port(port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    private void assertResourceNotFound(int port, String basePath) {
        Response response = given().basePath(basePath).port(port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(404).body("status", equalTo(404)).body("error", equalTo("Not Found"));
    }

    @Test
    public void implicitAutoTest() {

        int port = configureAndStartApp(properties);

        assertResourceFound(port, "sample-app");
        assertResourceNotFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

    }

    @Test
    public void explicitAutoTest() {

        int port = configureAndStartApp(properties);

        assertResourceFound(port, "sample-app");
        assertResourceNotFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

    }

    @BeforeClass
    public void before() {

        properties = new Properties();
        properties.put("management.endpoint.shutdown.enabled", "true");
        properties.put("management.endpoints.web.exposure.include", "health,info,shutdown");

    }

    @Test
    public void beansTest() {

        properties.put("resteasy.jaxrs.app.registration", "beans");

        int port = configureAndStartApp(properties);

        assertResourceFound(port, "sample-app");
        assertResourceNotFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");
    }

    @Test
    public void propertySpringBeanClassTest() {
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", "com.sample.app.configuration.JaxrsApplication");

        int port = configureAndStartApp(properties);

        assertResourceFound(port, "sample-app");
        assertResourceNotFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

    }

    @Test
    public void propertyNonSpringBeanClassTest() {
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", "com.test.NonSpringBeanJaxrsApplication");

        int port = configureAndStartApp(properties);

        assertResourceNotFound(port, "sample-app");
        assertResourceFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

    }

    @Test
    public void invalidClassTest() {
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", "com.foor.bar.NonExistentApplicationClass");

        try {
            configureAndStartApp(properties, false);

            Assert.fail("Expected exception, due to class not found, has not been thrown");
        } catch (Exception ex) {
            Assert.assertEquals(ex.getCause().getClass(), ClassNotFoundException.class);
            Assert.assertEquals(ex.getCause().getMessage(), "com.foor.bar.NonExistentApplicationClass");
        }
    }

    @Test
    public void scanningTest() {
        properties.put("resteasy.jaxrs.app.registration", "scanning");

        int port = configureAndStartApp(properties);

        assertResourceFound(port, "sample-app");
        assertResourceFound(port, "sample-app-test");
        assertResourceFound(port, "sample-app-test-two");
        assertResourceNotFound(port, "/");

    }

}
