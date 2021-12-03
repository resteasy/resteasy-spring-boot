# Sample application for Wildfly Deployment

This example is for deploying to Wildfly Java EE Full & Web Distribution.

## Running the Example

This example has embedded `maven-wildfly-plugin` to download and run WildFly server automatically, and it will deploy the sample project into the server. 

Using the following Maven command to run the example:

```bash
$ mvn wildfly:run
```

The above command will download WildFly server, start it, and then deploy the sample project.

## Testing it

Here is the command to access the server:

```bash
$ curl localhost:8080/sample-app/rest/hello
Hello, world!                  
```

From above we can see the `HelloResource` served the request and output response generated from `EchoBean`, which is a spring bean wired into `HelloResource`.
