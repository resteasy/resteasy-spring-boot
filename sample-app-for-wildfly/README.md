# Sample application for Wildfly Deployment

This example is same with `sample-app` but it's for deploying to Wildfly Java EE Full & Web Distribution.

Firstly you should check the `RESTEasy` dependency version used in this example by run the following command under this example's source code directory:

```bash
$ mvn dependency:tree | grep resteasy
...
[INFO] |  +- org.jboss.resteasy:resteasy-core:jar:4.5.3.Final:runtime
```

From above output we can see this example is currently using `resteasy-core:4.5.3.Final`, it means we need to upgrade Wildfly modules 

follow the RESTEasy document to upgrade the RESTEasy modules inside Wildfly:

* [RESTEASY-2554 Update section "Upgrading RESTEasy within WildFly" #2349](https://github.com/resteasy/Resteasy/pull/2349/files#diff-671eaa22e461edb2367f5dab7fe4c112R251)

Then start the Wildfly server:

```bash
$ ./standalone.sh
...
15:01:50,343 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0025: WildFly Full 19.0.0.Final (WildFly Core 11.0.0.Final) started in 3332ms - Started 316 of 582 services (374 services are lazy, passive or on-demand)
```

After the server started, use the `jboss-cli.sh` to connect to the server:

```bash
$ ./jboss-cli.sh
You are disconnected at the moment. Type 'connect' to connect to the server or 'help' for the list of supported commands.
[disconnected /] connect localhost
[standalone@localhost:9990 /]
```

In the command line console, use the `deploy` command to deploy this sample's WAR file:





## Testing it

Send a **POST** request message, containing the payload below, to FIXME [http://localhost:8080/sample-app/echo](http://localhost:8080/sample-app/rest/hello).

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
