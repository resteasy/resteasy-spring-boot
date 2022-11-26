package com.sample.app.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

public class InvalidClassTest extends ConfigurationIT {
    @Test
    public void invalidClassTest() {
        final Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", "com.foor.bar.NonExistentApplicationClass");

        try {
            configureAndStartApp(properties, false, com.sample.app.Application.class);
            Assert.fail("Expected exception, due to class not found, has not been thrown");
        } catch (Throwable ex) {
            Assert.assertEquals(ex.getCause().getClass(), ClassNotFoundException.class);
            Assert.assertEquals(ex.getCause().getMessage(), "com.foor.bar.NonExistentApplicationClass");
        }
    }
}
