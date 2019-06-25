package org.jboss.resteasy.springboot;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.util.SocketUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
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

    @BeforeClass
    public void setUp() {
        int appPort = SocketUtils.findAvailableTcpPort();

        RestAssured.basePath = "sample-app";
        RestAssured.port = appPort;

        System.setProperty("resteasy.async.job.service.enabled", "true");

        SpringApplication app = new SpringApplication(Application.class);
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
