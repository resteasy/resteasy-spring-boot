//package org.jboss.resteasy.springboot;
//
//import org.jboss.resteasy.springboot.sample.TestApplication1;
//import org.jboss.resteasy.springboot.sample.TestApplication2;
//import org.jboss.resteasy.springboot.sample.TestApplication4;
//import org.jboss.resteasy.springboot.sample.TestApplication5;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.testng.annotations.Ignore;
//import org.testng.annotations.Test;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class ExtraTest extends JaxrsAppRegistrationTest {
//
//    @Test
//    @Ignore
//    public void nullTest() {
//        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
//        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn(null);
//
//        Set<Class> expectedRegisteredAppClasses = new HashSet<Class>();
//        expectedRegisteredAppClasses.add(TestApplication1.class);
//        expectedRegisteredAppClasses.add(TestApplication2.class);
//        expectedRegisteredAppClasses.add(TestApplication4.class);
//        expectedRegisteredAppClasses.add(TestApplication5.class);
//
//        test(configurableEnvironmentMock, expectedRegisteredAppClasses);
//    }
//
//    @Ignore
//    @Test
//    public void autoTest() {
//        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
//        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("auto");
//
//        Set<Class> expectedRegisteredAppClasses = new HashSet<Class>();
//        expectedRegisteredAppClasses.add(TestApplication1.class);
//        expectedRegisteredAppClasses.add(TestApplication2.class);
//        expectedRegisteredAppClasses.add(TestApplication4.class);
//        expectedRegisteredAppClasses.add(TestApplication5.class);
//
//        test(configurableEnvironmentMock, expectedRegisteredAppClasses);
//    }
//
//    @Test
//    @Ignore
//    public void scanningTest() {
//        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
//        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("scanning");
//
//        Set<Class> expectedRegisteredAppClasses = new HashSet<Class>();
//        expectedRegisteredAppClasses.add(TestApplication1.class);
//        expectedRegisteredAppClasses.add(TestApplication2.class);
//        expectedRegisteredAppClasses.add(TestApplication4.class);
//        expectedRegisteredAppClasses.add(TestApplication5.class);
//
//        test(configurableEnvironmentMock, expectedRegisteredAppClasses);
//    }
//
//}
