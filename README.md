[![License](http://img.shields.io/:license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Github CI](https://github.com/resteasy/resteasy-spring-boot/actions/workflows/ci.yml/badge.svg)](https://github.com/resteasy/resteasy-spring-boot/actions)

# RESTEasy Spring Boot Starter

The RESTEasy Spring Boot starters can be used by any regular Spring Boot application that wants to have REST endpoints and prefers RESTEasy as the JAX-RS implementation. They integrate with Spring as expected, which means every JAX-RS REST resource that is also a Spring bean will be automatically auto-scanned, integrated, and available. There are two flavors of starters available that provide integration with the following types of application servers:
- Servlet based (e.g. Tomcat)
- Reactor Netty

## Features
* Enables RESTEasy for Spring Boot applications
* Supports JAX-RS providers, resources and sub-resources as Spring beans
* Supports automatic discovery and registration of multiple [JAX-RS Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html) classes as Spring beans
* Supports optional registration of [JAX-RS Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html) classes via class-path scanning, or manually, via configuration properties (or YAML) file
* Leverages and supports RESTEasy configuration
* Supports RESTEasy Asynchronous Job Service
* Servlet based server integration
* Reactor Netty integration

*This project has been kindly donated by PayPal. Please refer to https://github.com/paypal/resteasy-spring-boot for old versions.*

## Quick start

### Adding POM dependency
Add the Maven dependency below to your Spring Boot application pom file.<br>

**Servlet**

``` xml
<dependency>
   <groupId>org.jboss.resteasy</groupId>
   <artifactId>resteasy-servlet-spring-boot-starter</artifactId>
   <version>6.0.1-SNAPSHOT</version>
   <scope>runtime</scope>
</dependency>
```

**Reactor Netty**

``` xml
<dependency>
   <groupId>org.jboss.resteasy</groupId>
   <artifactId>resteasy-reactor-netty-spring-boot-starter</artifactId>
   <version>6.0.1-SNAPSHOT</version>
   <scope>runtime</scope>
</dependency>
```

### Registering JAX-RS application classes
Just define your JAX-RS application class (a subclass of [Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html)) as a Spring bean, and it will be automatically registered. See the example below.
See section _JAX-RS application registration methods_ in [How to use RESTEasy Spring Boot Starter](mds/USAGE.md) for further information.

``` java
package com.sample.app;

import org.springframework.stereotype.Component;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@Component
@ApplicationPath("/sample-app/")
public class JaxrsApplication extends Application {
}
```

### Registering JAX-RS resources and providers
Just define them as Spring beans, and they will be automatically registered.
Notice that JAX-RS resources can be singleton or request scoped, while JAX-RS providers must be singletons.

### Further information
See [How to use RESTEasy Spring Boot Starter](mds/USAGE.md).

## Core Projects

**Servlet**

  - **resteasy-servlet-spring-boot-starter**: The RESTEasy Spring Boot Starter project for servlet based application servers.
  - **resteasy-servlet-spring-boot-sample-app**: A simple Spring Boot application that exposes JAX-RS endpoints as Spring beans using RESTEasy via the RESTEasy Spring Boot servlet starter.
  - **resteasy-servlet-spring-boot-starter-test**: Integration tests for the RESTEasy Spring Boot servlet starter.
  
  
**Reactor Netty**  
  - **resteasy-reactor-netty-spring-boot-starter**: The RESTEasy Spring Boot Starter project using Reactor Netty as application server.
  - **resteasy-reactor-netty-spring-boot-sample-app**: A simple Spring Boot application that exposes JAX-RS endpoints as Spring beans using RESTEasy via the RESTEasy Spring Boot starter using Reactor Netty.
  - **resteasy-reactor-netty-spring-boot-starter-test**: Integration tests for the RESTEasy Spring Boot Starter with Reactor Netty as application server.
  

## Reporting an issue
Please open an issue using [JIRA](https://issues.jboss.org/browse/RESTEASY) (be sure to set *Spring / Spring Boot* in the *Component/s* field).

## Contacting us
To contact us, please use RESTEasy [mailing lists](http://resteasy.jboss.org/mailinglists).

## License
This project is licensed under the [Apache 2 License](License.html).
