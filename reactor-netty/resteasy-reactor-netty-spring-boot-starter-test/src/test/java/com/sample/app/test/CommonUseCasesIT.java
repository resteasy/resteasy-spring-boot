package com.sample.app.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.jboss.resteasy.springboot.ResteasyAutoConfiguration.ResteasyReactorNettyServerBean;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.sample.app.Application;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * This is an integration test based on a simple sample application and
 * very common use cases (see sample-app project)
 *
 */
public class CommonUseCasesIT {

    @BeforeClass
    public void startingApplicationUp() throws InterruptedException {

        // Start application
        final SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new LogbackTestApplicationListener());
        final ConfigurableApplicationContext appContext = springApplication.run("--server.port=" + 0);
        appContext.registerShutdownHook();
        
        // Find running port and configure RestAssured
        ResteasyReactorNettyServerBean serverBean = appContext.getBean(ResteasyReactorNettyServerBean.class);
        
        int appPort = 0;
        int maxWaitInSec = 5;
        
        while(appPort == 0 && maxWaitInSec > 0) {
            Thread.sleep(1000);//Allow for server to start
            maxWaitInSec--;
            appPort = serverBean.getServer().getPort();
        }    
        if(appPort == 0) {
            Assert.fail("Coudn't find server configured port within " + maxWaitInSec + " seconds.");
        }
        RestAssured.basePath = "sample-app";
        RestAssured.port = appPort;
        RestAssured.baseURI = "https://localhost";
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void happyPathTest() {
        final Response response = given().body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }
    
    @Test
    public void fieldBasedInjectionResourceTest() {
        final Response response = given().get("/foo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", any(String.class));
    }

    @Test
    public void filterTest() {
        final Response response = given().body("is there anybody out there?").header("ping", "ping").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?")).header("pong", equalTo("pong"));
    }

    @Test
    public void invalidUriPathTest() {
        // Notice "eco" is supposed to result in 404
        final Response response = given().body("is there anybody out there?").post("/eco");
        response.then().statusCode(404).body(equalTo("The resource you've requested, has not been found!"));
    }

    @Test
    public void invalidBaseUrlTest() {
        // Notice "sampl-ap" is missing both 'e' and 'p' and is supposed to result in 404
        final Response response = given().basePath("sampl-app").body("is there anybody out there?").post("/echo");
        response.then().statusCode(404).statusLine("HTTP/1.1 404 Not Found");
    }

    @Test
    public void invalidNoPayloadTest() {
        // Notice that the endpoint we are sending a request to uses Bean Validations to assure
        // the request message payload is valid. If that is not the case (a blank payload for example),
        // then the server is expected to return a 400 response message
        final Response response = given().body("").accept("application/json").post("/echo");
        response.then().statusCode(400).body("parameterViolations.message", hasItems("must not be empty"));
    }


}
