# Sample application for Wildfly Deployment

This example is same with `sample-app` but it's for deploying to Wildfly Java EE Full & Web Distribution.

First you should package this sample:

```bash
$ mvn package
```

And it will generate the WAR file:

```txt
works/resteasy-spring-boot/sample-app-for-wildfly/target/sample-app.war
```

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

```bash
[standalone@localhost:9990 /] deploy works/resteasy-spring-boot/sample-app-for-wildfly/target/sample-app.war --force
```

From server side we can see it's deployed:

```bash
16:30:42,046 INFO  [org.wildfly.extension.undertow] (ServerService Thread Pool -- 449) WFLYUT0021: Registered web context: '/sample-app' for server 'default-server'
```

## Testing it

Here is the command to access the server:

```bash
$ curl localhost:8080/sample-app/rest/hello
Hello, world!                  
```

From above we can see the `HelloResource` served the request and output response generated from `EchoBean`, which is a spring bean wired into `HelloResource`.
