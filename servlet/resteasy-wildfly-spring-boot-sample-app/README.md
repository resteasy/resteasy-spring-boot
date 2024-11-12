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
$ curl localhost:8080/rest/hello
Hello, world!                  
```

From above, we can see the `HelloResource` served the request and output response generated from `EchoBean`, which is a spring bean wired into `HelloResource`.


## Play with the Springdoc integration

The example includes the `springdoc` integration, and you can access the swagger-ui by it:

- http://localhost:8080/swagger-ui/index.html

And you can send the request to the `hello` method provided by the service.

To access the generated OpenAPI doc, you should use the following URL:

- http://localhost:8080/openapi.json

Or:

- http://localhost:8080/openapi.yaml

Here's the sample output:

```bash
➤ curl localhost:8080/openapi.json
{
  "openapi" : "3.0.1",
  "info" : {
    "title" : "my-rest-app",
    "description" : "My Rest App",
    "version" : "1.0.0"
  },
  "paths" : {
    "/rest/hello" : {
      "get" : {
        "operationId" : "get",
        "responses" : {
          "default" : {
            "description" : "return hello world",
            "content" : {
              "*/*" : {
                "schema" : {
                  "type" : "string"
                }
              }
            }
          }
        }
      }
    }
  }
}
```