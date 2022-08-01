package com.sample.app.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import java.util.Properties;
import org.jboss.resteasy.springboot.reactor.ResteasyAutoConfiguration.ResteasyReactorNettyServerBean;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.sample.app.Application;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Integration tests for RESTEasy Asynchronous Job Service
 */
public class AsyncJobIT {

    private static final String JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY = "resteasy.async.job.service.enabled";
    
    @BeforeClass
    public void setUp() throws InterruptedException {

        // Start app
        final Properties properties = new Properties();
        properties.put("server.servlet.context-parameters." + JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY, true);
        System.setProperty(JAXRS_APP_ASYNC_JOB_ENABLE_PROPERTY, "true");

        final SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(properties);
        app.addListeners(new LogbackTestApplicationListener());
        
        final ConfigurableApplicationContext appContext = app.run("--server.port=" + 0);
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
    public void regularRequestTest() {
        final Response response = given().body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    @Test
    public void asyncRequestTest() {
        final Response response = given().body("is there anybody out there?").post("/echo?asynch=true");
        response.then().statusCode(202).body(is(emptyString()));

        final String location = response.getHeader("Location");
        final Response response1 = given().basePath("/").get(location + "?wait=1000");
        response1.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    @Test
    public void fireAndForgetRequestTest() {
        final Response response = given().body("is there anybody out there?").post("/echo?oneway=true");
        response.then().statusCode(202).body(is(emptyString()));
    }

}
