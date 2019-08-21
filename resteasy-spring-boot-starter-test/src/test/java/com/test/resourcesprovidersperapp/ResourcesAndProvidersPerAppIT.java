//package com.paypal.springboot.resteasy;
package com.test.resourcesprovidersperapp;

//import com.sample.app.Application;
//import com.test.resourcesprovidersperapp.JaxrsApplicationV1;
import com.test.resourcesprovidersperapp.ResoucesAndProvidersPerApp;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.util.SocketUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * This is an integration test based on a simple sample application and
 * very common use cases (see sample-app project)
 *
 * @author facarvalho
 */
public class ResourcesAndProvidersPerAppIT {

    @BeforeClass
    public void startingApplicationUp() {
        RestAssured.basePath = "/resourcesproviders/";
        int port = SocketUtils.findAvailableTcpPort();
        RestAssured.port = port;
        System.out.println("::: " + port);
        SpringApplication springApplication = new SpringApplication(ResoucesAndProvidersPerApp.class);
        springApplication.run("--server.port=" + port).registerShutdownHook();
//        try {
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @AfterClass
    public void shuttingDownApplication() {
        Response response = given().basePath("/").contentType("application/json").post("/actuator/shutdown");
        response.then().statusCode(200).body("message", equalTo("Shutting down, bye..."));    }

    @Test
    public void echoSucceeds() {
        Response response = given().body("oh behave").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo
("echoed v1 -> oh behave"));
    }

    @Test
    public void echoV2MustNotBeReachable() {
        Response response = given().body("oh behave").post("/echo2");
        Assert.assertNotEquals(200, response.getStatusCode(),"echo2 must not be reachable");
    }
}
