package com.sample.app.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;

import java.util.Properties;

import org.jboss.resteasy.springboot.common.utils.SocketUtils;
import org.springframework.boot.SpringApplication;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sample.app.Application;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Integration tests for RESTEasy Asynchronous Job Service
 *
 * @author facarvalho
 */
public class AsyncJobIT {

    private final String JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY = "resteasy.async.job.service.enabled";

    @BeforeClass
    public void setUp() {
        int appPort = SocketUtils.findAvailableTcpPort();

        RestAssured.basePath = "sample-app";
        RestAssured.port = appPort;

        Properties properties = new Properties();
        properties.put("server.servlet.context-parameters." + JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY, true);

        System.setProperty(JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY, "true");

        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(properties);
        app.addListeners(new LogbackTestApplicationListener());
        app.run("--server.port=" + appPort).registerShutdownHook();
    }

    @Test
    public void regularRequestTest() {
        Response response = given().body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    @Test
    public void asyncRequestTest() {
        Response response = given().body("is there anybody out there?").post("/echo?asynch=true");
        response.then().statusCode(202).body(isEmptyString());

        String location = response.getHeader("Location");
        response = given().get(location + "?wait=1000");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    @Test
    public void fireAndForgetRequestTest() {
        Response response = given().body("is there anybody out there?").post("/echo?oneway=true");
        response.then().statusCode(202).body(isEmptyString());
    }

    @AfterClass
    public void shuttingDownApplication() {
        Response response = given().basePath("/").contentType("application/json").post("/actuator/shutdown");
        response.then().statusCode(200).body("message", equalTo("Shutting down, bye..."));
    }

}
