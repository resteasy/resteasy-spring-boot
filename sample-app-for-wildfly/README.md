# Sample application for Wildfly Deployment

This example is same with `sample-app` but it's for deploying to Wildfly Java EE Full & Web Distribution.

Firstly you should follow the RESTEasy document to upgrade the RESTEasy modules inside Wildfly:

> TODO

Then start the Wildfly server and deploy the project WAR file:

> TODO


## Testing it

Send a **POST** request message, containing the payload below, to FIXME [http://localhost:8080/sample-app/echo](http://localhost:8080/sample-app/echo).

```
    is there anybody out there?
```

You should receive a response message with a payload similar to this as result:

``` json
    {
        "timestamp": "1484775122357",
        "echoText": "is there anybody out there?"
    }
```

The request message payload can be anything as plain text.
The response message is supposed to echo that, plus a timestamp of the moment the echo response was created on the server side. The response message will be in JSON format.
