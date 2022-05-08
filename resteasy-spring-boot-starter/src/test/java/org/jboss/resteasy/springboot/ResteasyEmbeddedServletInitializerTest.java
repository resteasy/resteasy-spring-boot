package org.jboss.resteasy.springboot;

import java.util.Map;

import org.jboss.resteasy.springboot.sample.TestProviderNoBean;
import org.jboss.resteasy.springboot.sample.TestResourceNoBean;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

/**
 * Created by facarvalho on 11/25/15.
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@ContextConfiguration("classpath:test-config.xml")
public class ResteasyEmbeddedServletInitializerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ApplicationContext applicationContext;
    
    @BeforeClass
    public static void setup() {
        System.setProperty("resteasy.jaxrs.scan-packages", "org.jboss.resteasy.springboot.sample");        
    }
    
    @AfterClass
    public static void cleanup() {
        System.clearProperty("resteasy.jaxrs.scan-packages");
    }

    @Test
    public void postProcessBeanFactory() {
        
        Map<String, ServletRegistrationBean> servletRegistrationBeans = applicationContext.getBeansOfType(ServletRegistrationBean.class);
        Assert.assertNotNull(servletRegistrationBeans);

        // Although there are 5 sample JAX-RS Application classes, one of them is not annotated with the ApplicationPath annotation!
        Assert.assertEquals(servletRegistrationBeans.size(), 4);

        for(String applicationClassName : servletRegistrationBeans.keySet()) {
            testApplicaton(applicationClassName, servletRegistrationBeans.get(applicationClassName));
        }
        
        // check if the scanned resources and providers are found
        Assert.assertNotNull(applicationContext.getBeansOfType(TestProviderNoBean.class));
        Assert.assertNotNull(applicationContext.getBeansOfType(TestResourceNoBean.class));
    }

    private void testApplicaton(String applicationClassName, ServletRegistrationBean servletRegistrationBean) {
        Assert.assertEquals(applicationClassName, servletRegistrationBean.getServletName());
        Assert.assertTrue(servletRegistrationBean.isAsyncSupported());
        Assert.assertEquals(applicationClassName, servletRegistrationBean.getInitParameters().get("jakarta.ws.rs.Application"));        
                
    }

}
