package org.jboss.resteasy.springboot;

import java.util.Optional;
import java.util.Set;

import org.jboss.resteasy.springboot.sample.TestApplication1;
import org.jboss.resteasy.springboot.sample.TestApplication2;
import org.jboss.resteasy.springboot.sample.TestApplication3;
import org.jboss.resteasy.springboot.sample.TestApplication4;
import org.jboss.resteasy.springboot.sample.TestApplication5;
import org.jboss.resteasy.springboot.sample.TestProvider1;
import org.jboss.resteasy.springboot.sample.TestResource1;
import org.jboss.resteasy.springboot.sample.TestResource2;
import org.jboss.resteasy.springboot.utilities.CustomPropertySource;
import org.jboss.resteasy.springboot.utilities.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.netty.handler.ssl.ClientAuth;
import jakarta.ws.rs.core.Application;

public class ResteasyBeansProcessorTest extends AbstractTestNGSpringContextTests {
	
    private static final Logger logger = LoggerFactory.getLogger(ResteasyBeansProcessorTest.class);


    private final String SPRING_CONTEXT_FILE_LOCATION = "classpath:test-config.xml";
    private final String SPRING_CONTEXT_FILE_NO_RESOURCES_LOCATION = "classpath:test-config-no-resources.xml";
    
    private enum JaxrsAppClassesRegistration {
        BEANS, PROPERTY, SCANNING, AUTO
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void unknownAppRegistrationPropValueTest() {
        
        final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition("Unknown"); 
        
        final ReactorNettyServerConfig serverConfig = new ReactorNettyServerConfig.Builder()
                .withClientAuth(ClientAuth.OPTIONAL)
                .withIdleTimeout(null)
                .withSecurityDomain(null)
                .withSSLContext(null)
                .withPort(0)
                .build();
        
        final ApplicationContext appContext = TestUtils.configureAndCreateAppContext(
                SPRING_CONTEXT_FILE_LOCATION, customPropertySource, Optional.of(serverConfig));

        ((ConfigurableApplicationContext)appContext).close();
    }
    
    
    @Test(expectedExceptions = {ClassNotFoundException.class, BeansException.class})
    public void appNotFoundTest() {

		final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.PROPERTY.name())
                .setAppClasses("com.foor.bar.NonExistentApplicationClass"); 
		
        final ApplicationContext appContext = TestUtils.configureAndCreateAppContext(
                SPRING_CONTEXT_FILE_LOCATION, customPropertySource, Optional.empty());
        
        ((ConfigurableApplicationContext)appContext).close();
    }
    
    @Test
    public void allAppsProvidersAndResourcesFoundTest() {
    	
        final ApplicationContext appContext = TestUtils.configureAndCreateAppContext(
                SPRING_CONTEXT_FILE_LOCATION, new CustomPropertySource(), Optional.empty());
        
    	final ResteasyBeanProcessorReactorNetty resteasyBeansProcessor = appContext.getBean(ResteasyBeanProcessorReactorNetty.class);
        
    	final Set<Class<? extends Application>> applications = resteasyBeansProcessor.getApplications();
        		
        Assert.assertEquals(applications.size(), 4);
        Assert.assertTrue(applications.contains(TestApplication1.class));
        Assert.assertTrue(applications.contains(TestApplication2.class));
        Assert.assertFalse(applications.contains(TestApplication3.class)); // Will not be included since not annotated
        Assert.assertTrue(applications.contains(TestApplication4.class));
        Assert.assertTrue(applications.contains(TestApplication5.class));
        
        verifyProvidersAndResources(resteasyBeansProcessor);
        
        ((ConfigurableApplicationContext)appContext).close();
        
    }
    
    @Test
    public void oneAppAllProvidersAndResourcesFoundTest() {
    	
		final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.PROPERTY.name())
                .setAppClasses(TestApplication1.class.getTypeName());
		
		final ApplicationContext appContextOneJaxrsApp = TestUtils.configureAndCreateAppContext(
				SPRING_CONTEXT_FILE_LOCATION, customPropertySource, Optional.empty());
        
		final ResteasyBeanProcessorReactorNetty resteasyBeansProcessor = appContextOneJaxrsApp.getBean(ResteasyBeanProcessorReactorNetty.class);
        
		final Set<Class<? extends Application>> applications = resteasyBeansProcessor.getApplications();
        
        Assert.assertEquals(applications.size(), 1);
        Assert.assertTrue(applications.contains(TestApplication1.class));
        verifyProvidersAndResources(resteasyBeansProcessor);
        
        ((ConfigurableApplicationContext)appContextOneJaxrsApp).close();
    }
    
	@Test
	public void noAppAllProvidersAndResourcesFoundTest() {

		final CustomPropertySource customPropertySource = new CustomPropertySource()
                .setAppClassesDefinition(JaxrsAppClassesRegistration.BEANS.name())
                .setIsAsyncEnabled(true);
		
	    final ApplicationContext appContext = TestUtils.configureAndCreateAppContext(
	              SPRING_CONTEXT_FILE_LOCATION, customPropertySource, Optional.empty());

        final ResteasyBeanProcessorReactorNetty resteasyBeansProcessor = appContext
                .getBean(ResteasyBeanProcessorReactorNetty.class);

		Assert.assertEquals(resteasyBeansProcessor.getApplications().size(), 0);
		verifyProvidersAndResources(resteasyBeansProcessor);

		((ConfigurableApplicationContext) appContext).close();

	}
    
    @Test
    public void noResourcesFoundTest() {

		final ApplicationContext appContext = TestUtils.configureAndCreateAppContext(
		        SPRING_CONTEXT_FILE_NO_RESOURCES_LOCATION, new CustomPropertySource(), Optional.empty());
		
    	final ResteasyBeanProcessorReactorNetty resteasyBeansProcessor = appContext.getBean(ResteasyBeanProcessorReactorNetty.class);
        
        Assert.assertEquals(resteasyBeansProcessor.getProviders().size(), 0);
        Assert.assertEquals(resteasyBeansProcessor.getAllResources().size(), 0);
        
        ((ConfigurableApplicationContext)appContext).close();
                
    }
    
    
    @Test
    public void reactorNettyServerDefaultConfigTest() {

        final ReactorNettyServerConfig config = ReactorNettyServerConfig.defaultConfig(new StandardEnvironment());
        Assert.assertNotNull(config.getPort());
        Assert.assertEquals(config.getPort(), (Integer)8080);
        Assert.assertEquals(config.getClientAuth(), ClientAuth.REQUIRE);
        Assert.assertNull(config.getSslContext());
        Assert.assertNull(config.getIdleTimeout());
        Assert.assertNull(config.getSecurityDomain());
        Assert.assertNull(config.getCleanupTasks());
    }

    /**
     * Verify that clean up tasks are setup correctly
     */
    @Test
    public void reactorNettyCleanupTasksConfigTest() {
        Runnable echo = () -> logger.info("test");
        final ReactorNettyServerConfig config = new ReactorNettyServerConfig.Builder().
                withCleanupTasks(echo)
                .build();
                                                
        Assert.assertNotNull(config.getCleanupTasks());
        Assert.assertEquals(config.getCleanupTasks().get(0), echo);
    }
    
    private void verifyProvidersAndResources(final ResteasyBeanProcessorReactorNetty resteasyBeansProcessor) {
    	
    	final Set<Class<?>> providers = resteasyBeansProcessor.getProviders();
    	
        Assert.assertNotNull(providers);
        Assert.assertEquals(providers.size(), 1);
        Assert.assertTrue(providers.contains(TestProvider1.class));
        
        final Set<Class<?>> allResources = resteasyBeansProcessor.getAllResources();
        
        Assert.assertNotNull(allResources);
        Assert.assertEquals(allResources.size(), 2);
        Assert.assertTrue(allResources.contains(TestResource1.class));
        Assert.assertTrue(allResources.contains(TestResource2.class));
        
    }

}
