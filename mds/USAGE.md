# How to use RESTEasy Spring Boot Starter

#### Adding POM dependency

Add the Maven dependency below to your Spring Boot application pom file.<br>

``` xml
<dependency>
   <groupId>org.jboss.resteasy</groupId>
   <artifactId>resteasy-spring-boot-starter</artifactId>
   <version>4.6.3.Final</version>
   <scope>runtime</scope>
</dependency>
```

#### Registering JAX-RS application classes

Just define your JAX-RS application class (a subclass of [Application](https://github.com/eclipse-ee4j/jaxrs-api/blob/master/jaxrs-api/src/main/java/jakarta/ws/rs/core/Application.java)) as a Spring bean, and it will be automatically registered. See the example below.
See section [JAX-RS Application, Resources and Sub-Resources](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html) for further information.

``` java
package com.sample.app;

import org.springframework.stereotype.Component;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@Component
@ApplicationPath("/sample-app/")
public class JaxrsApplication extends Application {
}
```

#### Registering JAX-RS resources and providers

Just define them as Spring beans, and they will be automatically registered.
Notice that JAX-RS resources can be singleton or request scoped, while JAX-RS providers must be singletons.

## Advanced topics

#### JAX-RS application registration methods

JAX-RS applications are defined via sub-classes of [Application](https://github.com/eclipse-ee4j/jaxrs-api/blob/master/jaxrs-api/src/main/java/jakarta/ws/rs/core/Application.java). One or more JAX-RS applications can be registered, and there are three different methods to do so:

1. By having them defined as Spring beans.
2. By setting property `resteasy.jaxrs.app.classes` via Spring Boot configuration file (properties or YAML). This property should contain a comma separated list of JAX-RS sub-classes.
3. Automatically by classpath scanning (looking for `javax.ws.rs.core.Application` sub-classes). **See important note number 6 about this method**.

You can define the method you prefer by setting property `resteasy.jaxrs.app.registration` (via Spring Boot configuration file), although you don't have to, in that case the `auto` method is the default. The possible values are:

- `beans`
- `property`
- `scanning`
- `auto` (default)

The first three values refer respectively to each one of the three methods described earlier. The last one, `auto`, when set (or when property `resteasy.jaxrs.app.registration` is not present), attempts first to find JAX-RS application classes by searching them as Spring beans. If any is found, the search stops, and those are the only JAX-RS applications to be registered. If no JAX-RS application Spring beans are found, then the `property` approach is tried. If still no JAX-RS application classes could be found, then the last method, `scanning`, is attempted. If after that still no JAX-RS application class could be registered, then a default one will be automatically created mapping to `/*` (_according to section 2.3.2 in the JAX-RS 2.0 specification_).

__Important notes__

- If no JAX-RS application classes are found, a default one will be automatically created mapping to `/*` (_according to section 2.3.2 in the JAX-RS 2.0 specification_). Notice that, in this case, if you have any other Servlet in your application, their URL matching might conflict. For example, if you have Spring Boot actuator and its mapped to `/`, its endpoints might not be reachable.
- It is recommended to always have at least one JAX-RS application class.
- A JAX-RS application class with no `javax.ws.rs.ApplicationPath` annotation will not be registered, unless `resteasy.servlet.mapping.prefix` is specified.
- Avoid setting the JAX-RS application base URI to simply `/` to prevent URI conflicts, as explained in item 1.
- Property `resteasy.jaxrs.app` was deprecated in version *2.2.0-RELEASE* (see [issue 35](https://github.com/paypal/resteasy-spring-boot/issues/35)) 
and replaced `resteasy.jaxrs.app.classes`.  Property `resteasy.jaxrs.app` has been fully removed from version 4.0.1.Final.
- Starting on version 3.0.0, the behavior of the `scanning` JAX-RS Application subclass registration method will change, being more restrictive. Instead of scanning the whole classpath, it will scan only packages registered to be scanned by Spring framework (regardless of the JAX-RS Application subclass being a Spring bean or not). The reason is to improve application startup performance. Having said that, it is recommended that every application use any method, other than `scanning`. Or, if using `scanning`, make sure your JAX-RS Application subclass is under a package to be scanned by Spring framework. If not, starting on version 3.0.0,it won't be found.
- When no JAX-RS Application is configured, the property `resteasy.jaxrs.defaultPath` can be used to define the base path. It defaults to `/` if not set
- `resteasy.servlet.mapping.prefix` will override `javax.ws.rs.ApplicationPath` if both are specified.

#### RESTEasy configuration

RESTEasy offers a few configuration switches, [as seen here](https://docs.jboss.org/resteasy/docs/4.5.7.Final/userguide/html_single/index.html#configuration_switches), and they are set as Servlet context init parameters. In Spring Boot, Servlet context init parameters are defined via Spring Boot `application.properties` file, using the property prefix `server.servlet.context-parameters.*` (search for it in [Spring Boot reference guide](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)).</br>

As an example, to set RESTEasy property `resteasy.role.based.security` to `true`, just add the property bellow to Spring Boot `application.properties` file.

```
server.servlet.context-parameters.resteasy.role.based.security=true
```

It is important to mention that the following RESTEasy configuration options are NOT applicable to an application using RESTEasy Spring Boot starter.
All other RESTEasy configuration options are supported normally.

| Configuration option | Why it is not applicable |
|---|---|
|`javax.ws.rs.Application`|JAX-RS application classes are registered as explained in section _"JAX-RS application registration methods"_ above|
|`resteasy.scan`<br/>`resteasy.scan.providers`<br/>`resteasy.scan.resources`<br/>`resteasy.providers`<br/>`resteasy.use.builtin.providers`<br/>`resteasy.resources`<br/>`resteasy.jndi.resources`|All JAX-RS resources and providers are always supposed to be Spring beans, and they are automatically discovered|
