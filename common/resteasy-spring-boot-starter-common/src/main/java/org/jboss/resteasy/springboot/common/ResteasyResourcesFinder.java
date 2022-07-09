package org.jboss.resteasy.springboot.common;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;


/**
 * Helper class that finds JAX-RS classes annotated with {@link jakarta.ws.rs.Path} and with
 * {@link jakarta.ws.rs.ext.Provider}.
 */
public class ResteasyResourcesFinder {
	
	private static final Logger logger = LoggerFactory.getLogger(ResteasyResourcesFinder.class);
	
	/**
	 * This is how {@code JAXRS_APP_CLASSES_PROPERTY} was named originally. It conflicted with {@code resteasy.jaxrs.app.registration}<br>
	 * in case of YAML files, since registration was a child of app from an YAML perspective, which is not allowed.<br>
	 * Because of that its name was changed (the ".classes" suffix was added).
	 * This legacy property has not been removed though, to keep backward compatibility, but it is marked as deprecated. It will be
	 * available only for {@code .properties} files, but not for {@code YAML} files. It should be finally removed in a future major release.
	 */
	private static final String JAXRS_APP_CLASSES_PROPERTY_LEGACY = "resteasy.jaxrs.app";
    private static final String JAXRS_APP_CLASSES_PROPERTY = "resteasy.jaxrs.app.classes";
    private static final String JAXRS_APP_CLASSES_DEFINITION_PROPERTY = "resteasy.jaxrs.app.registration";
    
    
    private enum JaxrsAppClassesRegistration {
        BEANS, PROPERTY, SCANNING, AUTO
    }
	
	private Set<Class<? extends Application>> applications = new HashSet<Class<? extends Application>>();
	private final Set<Class<?>> allResources = new HashSet<Class<?>>();
	private final Set<Class<?>> providers = new HashSet<Class<?>>();

    /*
     * Find the JAX-RS application classes.
     * This is done by one of these three options in this order:
     *
     * 1- By having them defined as Spring beans
     * 2- By setting property {@code resteasy.jaxrs.app.classes} via Spring Boot application properties file.
     *    This property should contain a comma separated list of JAX-RS sub-classes
     * 3- Via classpath scanning (looking for javax.ws.rs.core.Application sub-classes)
     *
     * First try to find JAX-RS Application sub-classes defined as Spring beans. If that is existent,
     * the search stops, and those are the only JAX-RS applications to be registered.
     * If no JAX-RS application Spring beans are found, then see if Spring Boot property {@code resteasy.jaxrs.app.classes}
     * has been set. If it has, the search stops, and those are the only JAX-RS applications to be registered.
     * If not, then scan the classpath searching for JAX-RS applications.
     *
     * There is a way though to force one of the options above, which is by setting property
     * {@code resteasy.jaxrs.app.registration} via Spring Boot application properties file. The possible valid
     * values are {@code beans}, {@code property}, {@code scanning} or {@code auto}. If this property is not
     * present, the default value is {@code auto}, which means every approach will be tried in the order and way
     * explained earlier.
     *
     * @param beanFactory
     */
    public void findJaxrsApplications(ConfigurableListableBeanFactory beanFactory) {
        logger.info("Finding JAX-RS Application classes");

        final JaxrsAppClassesRegistration registration = getJaxrsAppClassesRegistration(beanFactory);

        switch (registration) {
            case AUTO:
                findJaxrsApplicationBeans(beanFactory);
                if(applications.isEmpty()) findJaxrsApplicationProperty(beanFactory);
                if(applications.isEmpty()) findJaxrsApplicationScanning(beanFactory);
                break;
            case BEANS:
                findJaxrsApplicationBeans(beanFactory);
                break;
            case PROPERTY:
                findJaxrsApplicationProperty(beanFactory);
                break;
            case SCANNING:
                findJaxrsApplicationScanning(beanFactory);
                break;
            default:
                logger.error("JAX-RS application registration method (%s) not known, no application will be configured", registration.name());
                break;
        }
        
        applications = applications.stream().filter(app -> {
            final ApplicationPath path = AnnotationUtils.findAnnotation(app, ApplicationPath.class);
            if (path == null) {
                logger.warn("JAX-RS Application class {} has no ApplicationPath annotation, so it will not be configured", app.getName());
            } else {
                logger.info("JAX-RS Application class found: {}", ((Class<Application>) app).getName());
            }
            return path != null;
        }).collect(Collectors.toSet());

    }
    
    
    private JaxrsAppClassesRegistration getJaxrsAppClassesRegistration(ConfigurableListableBeanFactory beanFactory) {
        final ConfigurableEnvironment configurableEnvironment = beanFactory.getBean(ConfigurableEnvironment.class);
        final String jaxrsAppClassesRegistration = configurableEnvironment.getProperty(JAXRS_APP_CLASSES_DEFINITION_PROPERTY);
        JaxrsAppClassesRegistration registration = JaxrsAppClassesRegistration.AUTO;

        if(jaxrsAppClassesRegistration == null) {
            logger.info("Property {} has not been set, JAX-RS Application classes registration is being set to AUTO", JAXRS_APP_CLASSES_DEFINITION_PROPERTY);
        } else {
            logger.info("Property {} has been set to {}", JAXRS_APP_CLASSES_DEFINITION_PROPERTY, jaxrsAppClassesRegistration);
            try {
                registration = JaxrsAppClassesRegistration.valueOf(jaxrsAppClassesRegistration.toUpperCase());
            } catch(IllegalArgumentException ex) {
				final String errorMesage = String.format(
						"Property %s has not been properly set, value %s is invalid. JAX-RS Application classes registration is being set to AUTO.",
						JAXRS_APP_CLASSES_DEFINITION_PROPERTY, jaxrsAppClassesRegistration);
                logger.error(errorMesage);
                throw new IllegalArgumentException(errorMesage, ex);
            }
        }

        return registration;
    }

    /*
     * Find JAX-RS application classes by searching for their related
     * Spring beans
     *
     * @param beanFactory
     */
    private void findJaxrsApplicationBeans(ConfigurableListableBeanFactory beanFactory) {
        logger.info("Searching for JAX-RS Application Spring beans");

        final Map<String, Application> applicationBeans = beanFactory.getBeansOfType(Application.class, true, false);
        if(applicationBeans == null || applicationBeans.isEmpty()) {
            logger.info("No JAX-RS Application Spring beans found");
            return;
        }

        for (Application application : applicationBeans.values()) {
            applications.add(application.getClass());
        }
    }

    /*
     * Find JAX-RS application classes via property {@code resteasy.jaxrs.app.classes}
     */
    private void findJaxrsApplicationProperty(ConfigurableListableBeanFactory beanFactory) {
        final ConfigurableEnvironment configurableEnvironment = beanFactory.getBean(ConfigurableEnvironment.class);
        String jaxrsAppsProperty = configurableEnvironment.getProperty(JAXRS_APP_CLASSES_PROPERTY);
        if(jaxrsAppsProperty == null) {
            jaxrsAppsProperty = configurableEnvironment.getProperty(JAXRS_APP_CLASSES_PROPERTY_LEGACY);
            if(jaxrsAppsProperty == null) {
                logger.info("No JAX-RS Application set via property {}", JAXRS_APP_CLASSES_PROPERTY);
                return;
            }
			logger.warn(
					"Property {} has been set. Notice that this property has been deprecated and will be removed soon. Please replace it by property {}",
					JAXRS_APP_CLASSES_PROPERTY_LEGACY, JAXRS_APP_CLASSES_PROPERTY);
        } else {
            logger.info("Property {} has been set to {}", JAXRS_APP_CLASSES_PROPERTY, jaxrsAppsProperty);
        }

        final String[] jaxrsClassNames = jaxrsAppsProperty.split(",");

        for(String jaxrsClassName : jaxrsClassNames) {
            Class<? extends Application> jaxrsClass = null;
            try {
                jaxrsClass = (Class<? extends Application>) Class.forName(jaxrsClassName.trim());
            } catch (ClassNotFoundException e) {
                final String exceptionMessage = String.format("JAX-RS Application class %s has not been found", jaxrsClassName.trim());
                logger.error(exceptionMessage, e);
                throw new BeansException(exceptionMessage, e){};
            }
            applications.add(jaxrsClass);
        }
    }

    /*
     * Find JAX-RS application classes by scanning the classpath under
     * packages already marked to be scanned by Spring framework
     */
    private void findJaxrsApplicationScanning(BeanFactory beanFactory) {
        final List<String> packagesToBeScanned = getSpringApplicationPackages(beanFactory);

        final Set<Class<? extends Application>> applications = JaxrsApplicationScanner.getApplications(packagesToBeScanned);
        if(applications == null || applications.isEmpty()) {
            return;
        }
        this.applications.addAll(applications);
    }

    /*
     * Return the name of the packages to be scanned by Spring framework
     */
    private List<String> getSpringApplicationPackages(BeanFactory beanFactory) {
        return AutoConfigurationPackages.get(beanFactory);
    }

    /*
     * Search for JAX-RS resource and provider Spring beans,
     * which are the ones whose classes are annotated with
     * {@link Path} or {@link Provider} respectively
     *
     * @param beanFactory
     */
    public void findJaxrsResourcesAndProviderClasses(ConfigurableListableBeanFactory beanFactory) {
        logger.debug("Finding JAX-RS resources and providers Spring bean classes");

        final String[] resourceBeans = beanFactory.getBeanNamesForAnnotation(Path.class);
        final String[] providerBeans = beanFactory.getBeanNamesForAnnotation(Provider.class);

        if(resourceBeans != null) {
            for(String resourceBean : resourceBeans) {
                allResources.add(beanFactory.getType(resourceBean));
            }
        }
        
        if (this.getAllResources().isEmpty()) {
            logger.warn("No JAX-RS resource Spring beans have been found");
        }

        if(providerBeans != null) {
            for(String providerBean : providerBeans) {
                providers.add(beanFactory.getType(providerBean));
            }
        }

        if(logger.isDebugEnabled()) {
            for (Object resourceClass : allResources.toArray()) {
                logger.debug("JAX-RS resource class found: {}", ((Class) resourceClass).getName());
            }
        }
        if(logger.isDebugEnabled()) {
            for (Object providerClass: providers.toArray()) {
                logger.debug("JAX-RS provider class found: {}", ((Class) providerClass).getName());
            }
        }
    }

    public Set<Class<? extends Application>> getApplications() {
        return this.applications;
    }

    public Set<Class<?>> getAllResources() {
        return this.allResources;
    }

    public Set<Class<?>> getProviders() {
        return this.providers;
    }
    
}