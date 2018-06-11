[![License](http://img.shields.io/:license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

<!-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ -->
<!-- Set-up TravisCI, Codacy and deploy a first release with RESTEasy group id and artifact id, then uncomment and adjust accordingly the lines below replacing ?????? by correct values -->
<!-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ -->
[![Build Status](https://travis-ci.org/resteasy/resteasy-spring-boot.svg?branch=master)](https://travis-ci.org/resteasy/resteasy-spring-boot)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b30b165246e64b0a8b920acb389333bc)](https://www.codacy.com/app/asoldano/resteasy-spring-boot)
[![Codacy Coverage Badge](https://api.codacy.com/project/badge/Coverage/b30b165246e64b0a8b920acb389333bc)](https://www.codacy.com/app/asoldano/resteasy-spring-boot)
<!-- [![Maven Central](https://maven-badges.herokuapp.com/maven-central/??????/resteasy-spring-boot-starter/badge.svg?style=flat)](http://search.maven.org/#search|ga|1|g:??????) -->

# RESTEasy Spring Boot Starter

This Spring Boot starter can be used by any regular Spring Boot application that wants to have REST endpoints and prefers RESTEasy as the JAX-RS implementation.

Also, this RESTEasy Spring Boot starter integrates with Spring as expected, which means every JAX-RS REST resource that is also a Spring bean will be automatically auto-scanned, integrated, and available.

## Features
* Enables RESTEasy for Spring Boot applications
* Supports JAX-RS providers, resources and sub-resources as Spring beans
* Supports automatic discovery and registration of multiple [JAX-RS Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html) classes as Spring beans
* Supports optional registration of [JAX-RS Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html) classes via class-path scanning, or manually, via configuration properties (or YAML) file
* Leverages and supports RESTEasy configuration
* Supports RESTEasy Asynchronous Job Service

*This project has been kindly donated by PayPal. Please refer to https://github.com/paypal/resteasy-spring-boot for old versions.*

## Quick start

### Adding POM dependency
Add the Maven dependency below to your Spring Boot application pom file.<br>

``` xml
<dependency>
   <groupId>org.jboss.resteasy</groupId>
   <artifactId>resteasy-spring-boot-starter</artifactId>
   <version>2.0.0.Final-SNAPSHOT</version>
   <scope>runtime</scope>
</dependency>
```

### Registering JAX-RS application classes
Just define your JAX-RS application class (a subclass of [Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html)) as a Spring bean, and it will be automatically registered. See the example below.
See section _JAX-RS application registration methods_ in [How to use RESTEasy Spring Boot Starter](mds/USAGE.md) for further information.

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

### Registering JAX-RS resources and providers
Just define them as Spring beans, and they will be automatically registered.
Notice that JAX-RS resources can be singleton or request scoped, while JAX-RS providers must be singletons.

### Further information
See [How to use RESTEasy Spring Boot Starter](mds/USAGE.md).

## Projects

  - **sample-app**: A simple Spring Boot application that exposes JAX-RS endpoints as Spring beans using RESTEasy via this RESTEasy Spring Boot starter.
  - **resteasy-spring-boot-starter**: The RESTEasy Spring Boot Starter project.
  - **resteasy-spring-boot-starter-test**: Integration tests for the RESTEasy Spring Boot Starter project.

## Reporting an issue
Please open an issue using [JIRA](https://issues.jboss.org/browse/RESTEASY) (be sure to set *Spring / Spring Boot* in the *Component/s* field).

## Contacting us
To contact us, please use RESTEasy [mailing lists](http://resteasy.jboss.org/mailinglists).

## License
This project is licensed under the [Apache 2 License](License.html).
