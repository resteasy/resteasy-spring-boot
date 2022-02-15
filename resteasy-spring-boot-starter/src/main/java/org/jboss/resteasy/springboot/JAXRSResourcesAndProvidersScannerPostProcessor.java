package org.jboss.resteasy.springboot;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

/**
 * Scanner bean factory post processor that is responsible for scanning classpath
 * (configured packages for JAX RS resources and providers).
 * 
 * It's meant to run as on of the first bean factory post processors so others, especially 
 * <code>org.jboss.resteasy.plugins.spring.SpringBeanProcessor</code> can find the bean definitions produced by this class.
 * 
 * This class is not active unless <code>resteasy.jaxrs.scan-packages</code> property is set.
 *
 */
public class JAXRSResourcesAndProvidersScannerPostProcessor implements BeanFactoryPostProcessor, PriorityOrdered {

    private static final Logger logger = LoggerFactory.getLogger(JAXRSResourcesAndProvidersScannerPostProcessor.class);
    private static final String JAXRS_SCAN_PACKAGES_PROPERTY = "resteasy.jaxrs.scan-packages";
    
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ConfigurableEnvironment configurableEnvironment = beanFactory.getBean(ConfigurableEnvironment.class);
        String jaxrsScanPackages = configurableEnvironment.getProperty(JAXRS_SCAN_PACKAGES_PROPERTY);

        
        Set<Class<?>> provicerClasses = findJaxrsResourcesOrProviderClasses(jaxrsScanPackages, Provider.class);
        for (Class<?> providerClazz : provicerClasses) {
            registerScannedBean(beanFactory, providerClazz);            
        }
        
        Set<Class<?>> resourceClasses = findJaxrsResourcesOrProviderClasses(jaxrsScanPackages, Path.class);
        for (Class<?> resourceClazz : resourceClasses) {
            registerScannedBean(beanFactory, resourceClazz);            
        }
        
    }
    
    /*
     * Creates singleton bean definition for found classes that represent either JAX RS resource or provider
     */
    private void registerScannedBean(ConfigurableListableBeanFactory beanFactory, Class<?> clazz) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        
        GenericBeanDefinition bean = new GenericBeanDefinition();
        bean.setBeanClass(clazz);
        bean.setAutowireCandidate(true);
        bean.setScope("singleton");
        
        registry.registerBeanDefinition(clazz.getName(), bean);
    }
    
    /*
     * Scan the classpath under the specified packages looking for JAX-RS resources and providers
     */
    private static Set<Class<?>> findJaxrsResourcesOrProviderClasses(String packagesToBeScanned, Class<? extends Annotation>  annotationType) {
        logger.info("Scanning classpath to find JAX-RS classes annotated with {}", annotationType);

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));

        Set<BeanDefinition> candidates = new HashSet<BeanDefinition>();
        Set<BeanDefinition> candidatesSubSet;

        for (String packageToScan : packagesToBeScanned.split(",")) {
            candidatesSubSet = scanner.findCandidateComponents(packageToScan.trim());
            candidates.addAll(candidatesSubSet);
        }

        Set<Class<?>> classes = new HashSet<>();
        ClassLoader classLoader = JAXRSResourcesAndProvidersScannerPostProcessor.class.getClassLoader();
        Class<?> type;
        for (BeanDefinition candidate : candidates) {
            try {
                type = (Class<?>) ClassUtils.forName(candidate.getBeanClassName(), classLoader);
                classes.add(type);
            } catch (ClassNotFoundException e) {
                logger.error("JAX-RS Resource/Provider could not be loaded", e);
            }
        }
        return classes;
    }
}
