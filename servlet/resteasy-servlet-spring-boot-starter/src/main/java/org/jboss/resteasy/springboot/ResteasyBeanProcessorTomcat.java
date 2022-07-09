package org.jboss.resteasy.springboot;

import org.jboss.resteasy.plugins.servlet.ResteasyServletInitializer;
import org.jboss.resteasy.springboot.common.ResteasyResourcesFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.*;

/**
 * This is a Spring version of {@link ResteasyServletInitializer}.
 * It does not register the servlets though, that is done by {@link ResteasyApplicationBuilder}
 * It only finds the JAX-RS Application classes (by scanning the classpath), and
 * the JAX-RS Path and Provider annotated Spring beans, and then register the
 * Spring bean definitions that represent each servlet registration.
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public class ResteasyBeanProcessorTomcat extends ResteasyResourcesFinder implements BeanFactoryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ResteasyBeanProcessorTomcat.class);

    private static final String JAXRS_DEFAULT_PATH = "resteasy.jaxrs.defaultPath";
    private static final String DEFAULT_BASE_APP_PATH = "/";

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        
        logger.debug("Post process bean factory has been called");

        findJaxrsApplications(beanFactory);

        // This is done by finding their related Spring beans
        findJaxrsResourcesAndProviderClasses(beanFactory);

        if (getApplications().size() == 0) {
            registerDefaultJaxrsApp(beanFactory);
            return;
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

        for (Class<? extends Application> applicationClass : getApplications()) {
            
            ApplicationPath path = AnnotationUtils.findAnnotation(applicationClass, ApplicationPath.class);
            logger.debug("registering JAX-RS application class " + applicationClass.getName());
            GenericBeanDefinition applicationServletBean = createApplicationServlet(applicationClass, path.value());
            registry.registerBeanDefinition(applicationClass.getName(), applicationServletBean);
            
        }

    }

    /**
     * Register a default JAX-RS application, in case no other is present in the application.
     * Read section 2.3.2 in JAX-RS 2.0 specification.
     *
     * @param beanFactory
     */
    private void registerDefaultJaxrsApp(ConfigurableListableBeanFactory beanFactory) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        ConfigurableEnvironment configurableEnvironment = beanFactory.getBean(ConfigurableEnvironment.class);
        String path = configurableEnvironment.getProperty(JAXRS_DEFAULT_PATH, DEFAULT_BASE_APP_PATH);
        GenericBeanDefinition applicationServletBean =
                createApplicationServlet(Application.class, path);

        logger.info("No JAX-RS Application classes have been found. A default, one mapped to '{}', will be registered.", path);
        registry.registerBeanDefinition(Application.class.getName(), applicationServletBean);
    }

    /**
     * Creates a Servlet bean definition for the given JAX-RS application
     *
     * @param applicationClass
     * @param path
     * @return a Servlet bean definition for the given JAX-RS application
     */
    private GenericBeanDefinition createApplicationServlet(Class<? extends Application> applicationClass, String path) {
        GenericBeanDefinition applicationServletBean = new GenericBeanDefinition();
        applicationServletBean.setFactoryBeanName(ResteasyApplicationBuilder.BEAN_NAME);
        applicationServletBean.setFactoryMethodName("build");

        Set<Class<?>> resources = getAllResources();

        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addIndexedArgumentValue(0, applicationClass.getName());
        values.addIndexedArgumentValue(1, path);
        values.addIndexedArgumentValue(2, resources);
        values.addIndexedArgumentValue(3, getProviders());
        applicationServletBean.setConstructorArgumentValues(values);

        applicationServletBean.setAutowireCandidate(false);
        applicationServletBean.setScope("singleton");

        return applicationServletBean;
    }

}
