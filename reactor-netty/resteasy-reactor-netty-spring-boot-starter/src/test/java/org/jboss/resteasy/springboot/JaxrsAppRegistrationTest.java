package org.jboss.resteasy.springboot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jboss.resteasy.springboot.sample.TestApplication1;
import org.jboss.resteasy.springboot.sample.TestApplication2;
import org.jboss.resteasy.springboot.sample.TestApplication3;
import org.jboss.resteasy.springboot.sample.TestApplication4;
import org.jboss.resteasy.springboot.sample.TestApplication5;
import org.jboss.resteasy.springboot.utilities.CustomPropertySource;
import org.jboss.resteasy.springboot.utilities.TestUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.Application;

public class JaxrsAppRegistrationTest extends AbstractTestNGSpringContextTests {
	
	private final String SPRING_CONTEXT_FILE_LOCATION = "classpath:test-config.xml";

	private enum JaxrsAppClassesRegistration {BEANS, PROPERTY, SCANNING, AUTO}
	
	private final List<Class<? extends Application>> expectedAppsAll = Stream
			.of(
			    TestApplication1.class, 
			    TestApplication2.class,
			    TestApplication3.class, 
			    TestApplication4.class, 
			    TestApplication5.class)
			.collect(Collectors.toList());		
	
	
    @Test
    public void nullTest() {
		verifyExpectedApps(expectedAppsAll, new CustomPropertySource());
	}
	
	@Test
    public void autoTest() {
	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.AUTO.name());
		verifyExpectedApps(expectedAppsAll, customPropertySource);
	}
	
	@Test
    public void scanningTest() {
	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.SCANNING.name());
		verifyExpectedApps(expectedAppsAll, customPropertySource);
	}
	
	@Test
    public void autoClassesSetTest() {

	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.AUTO.name())
                .setAppClasses(TestApplication3.class.getTypeName());
		
		final List<Class<? extends Application>> expectedApps = Stream
				.of(TestApplication3.class)
				.collect(Collectors.toList());		
		
		verifyExpectedApps(expectedApps, customPropertySource);
	}
	
	
	@Test
    public void beansTest() {
	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.BEANS.name());
		verifyExpectedApps(new ArrayList<>(), customPropertySource);
	}
	
	
	@Test
    public void propertyTest() {

	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.PROPERTY.name())
                .setAppClasses(TestApplication1.class.getTypeName() + "," + TestApplication2.class.getTypeName());
		
	    final List<Class<? extends Application>> expectedApps = Stream
				.of(
						TestApplication1.class,
						TestApplication2.class)
				.collect(Collectors.toList());		
		
		verifyExpectedApps(expectedApps, customPropertySource);

	}
	
	
	@Test
    public void propertyClassesNotSetTest() {
	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.PROPERTY.name());
		verifyExpectedApps(expectedAppsAll, customPropertySource);

	}
	
	@Test(expectedExceptions = {BeansException.class})
    public void propertyInvalidClassTest() {
	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.PROPERTY.name())
                .setAppClasses("does.not.exist.Com");
		verifyExpectedApps(null, customPropertySource);

	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidRegistrationTest() {
	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition("turkey");
		verifyExpectedApps(null, customPropertySource);
	}
	
	
	@Test
    public void legacyPropertyTest() {
	    final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.PROPERTY.name())
                .setAppClasses(TestApplication3.class.getTypeName() + "," + TestApplication5.class.getTypeName()); 
		
	    final List<Class<? extends Application>> expectedApps = Stream
				.of(
						TestApplication3.class,
						TestApplication5.class)
				.collect(Collectors.toList());		
		
		verifyExpectedApps(expectedApps, customPropertySource);
		
	}
	
	private void verifyExpectedApps (List<Class<? extends Application>> expectedApps, CustomPropertySource customPropertySource) {
		
        final ApplicationContext appContext = TestUtils.configureAndCreateAppContext(SPRING_CONTEXT_FILE_LOCATION,
                customPropertySource, Optional.empty());
		
	    final ResteasyBeanProcessorReactorNetty beanProcessor = appContext.getBean(ResteasyBeanProcessorReactorNetty.class);
		
		if(expectedApps == null) {
			Assert.assertEquals(beanProcessor.getApplications(), 0);
		}
		else {
			Assert.assertNotNull(beanProcessor.getApplications());
			expectedApps.forEach(app -> {
				beanProcessor.getApplications().contains(app);
			});
		}
		
		((ConfigurableApplicationContext)appContext).close();
        
	}


	
}
