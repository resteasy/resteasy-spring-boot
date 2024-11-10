package com.sample.app;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@ApplicationPath("/rest")
@OpenAPIDefinition(info = @Info(title = "RestApp", version = "1.0.0"))
public class RestApp extends Application {
}
