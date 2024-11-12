package com.sample.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(title = "my-rest-app", description = "My Rest App", version = "1.0.0"))
@ApplicationPath("/rest")
public class RestApp extends Application {
}
