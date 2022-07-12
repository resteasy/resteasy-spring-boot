package org.jboss.resteasy.springboot;

import org.jboss.resteasy.springboot.common.ResteasyResourcesFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * This class finds JAX-RS Application, Resource and Provider classes.
 */
public class ResteasyBeanProcessorReactorNetty extends ResteasyResourcesFinder implements BeanFactoryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ResteasyBeanProcessorReactorNetty.class);

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        
        logger.debug("Post process bean factory has been called");

        findJaxrsApplications(beanFactory);

        // This is done by finding their related Spring beans
        findJaxrsResourcesAndProviderClasses(beanFactory);
        
    }


}
