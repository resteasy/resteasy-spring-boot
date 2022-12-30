package org.jboss.resteasy.springboot;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.Application;

/**
 * Created by facarvalho on 11/25/15.
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@ContextConfiguration("classpath:test-config.xml")
public class ResteasyBeanProcessorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void postProcessBeanFactoryTest() {
        Map<String, ServletRegistrationBean> servletRegistrationBeans = applicationContext.getBeansOfType(ServletRegistrationBean.class);
        Assert.assertNotNull(servletRegistrationBeans);

        // Although there are 5 sample JAX-RS Application classes, one of them is not annotated with the ApplicationPath annotation!
        Assert.assertEquals(servletRegistrationBeans.size(), 4);

        for(String applicationClassName : servletRegistrationBeans.keySet()) {
            testApplication(applicationClassName, servletRegistrationBeans.get(applicationClassName));
        }
    }

    
    private void testApplication(String applicationClassName, ServletRegistrationBean servletRegistrationBean) {
        Assert.assertEquals(applicationClassName, servletRegistrationBean.getServletName());
        Assert.assertEquals(applicationClassName, servletRegistrationBean.getInitParameters().get("jakarta.ws.rs.Application"));
        Assert.assertTrue(servletRegistrationBean.isAsyncSupported());
    }

}