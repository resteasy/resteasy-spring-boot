package org.jboss.resteasy.springboot;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.springframework.boot.SpringApplication;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sample.app.MyApp;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * This is an integration test based on a simple sample application and
 * very common use cases (see sample-app project)
 *
 * @author facarvalho
 */
public class CommonUseCasesIT {

    @BeforeClass
    public void startingApplicationUp() {
        RestAssured.basePath = "sample-app";
        int port = SocketUtils.findAvailableTcpPort();
        RestAssured.port = port;

        SpringApplication springApplication = new SpringApplication(MyApp.class);
        springApplication.addListeners(new LogbackTestApplicationListener());
        springApplication.run("--server.port=" + port).registerShutdownHook();
    }

    @AfterClass
    public void shuttingDownApplication() {
        Response response = given().basePath("/").contentType("application/json").post("/actuator/shutdown");
        response.then().statusCode(200).body("message", equalTo("Shutting down, bye..."));
    }

    @Test
    public void happyPathTest() {
        Response response = given().body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    @Test
    public void filterTest() {
        Response response = given().body("is there anybody out there?").header("ping", "ping").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?")).header("pong", equalTo("pong"));
    }

    @Test
    public void invalidUriPathTest() {
        // Notice "eco" is supposed to result in 404
        Response response = given().body("is there anybody out there?").post("/eco");
        response.then().statusCode(404).body(equalTo("The resource you've requested, has not been found!"));
    }

    @Test
    public void invalidBaseUrlTest() {
        // Notice "sampl-ap" is supposed to result in 404
        Response response = given().basePath("sampl-app").body("is there anybody out there?").post("/echo");
        response.then().statusCode(404).body("status", equalTo(404)).body("error", equalTo("Not Found"));
    }

    @Test
    public void invalidNoPayloadTest() {
        // Notice that the endpoint we are sending a request to uses Bean Validations to assure
        // the request message payload is valid. If that is not the case (a blank payload for example),
        // then the server is expected to return a 400 response message
        Response response = given().body("").accept("application/json").post("/echo");
        response.then().statusCode(400).body("parameterViolations.message", hasItems("must not be empty"));
    }

    @Test
    public void actuatorTest() throws InterruptedException {
        Response response = given().basePath("/").get("/actuator/health");
        response.then().statusCode(200).body("status", equalTo("UP"));
    }

}
