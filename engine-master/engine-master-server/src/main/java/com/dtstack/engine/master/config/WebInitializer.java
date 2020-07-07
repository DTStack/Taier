package com.dtstack.engine.master.config;

import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebInitializer implements WebApplicationInitializer {
    private static final Logger logger = LoggerFactory.getLogger(WebInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(EnvironmentContext.class, MasterServerBeanConfig.class, CacheConfig.class, ThreadPoolConfig.class,
                MybatisConfig.class, MvcConfiguration.class);
        context.setServletContext(servletContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);
        servletContext.setInitParameter("contextConfigLocation", "classpath*:mybatis-config.xml");
        servletContext.addListener(ContextLoaderListener.class);

        System.setSecurityManager(new NoExitSecurityManager());
    }


    private static void setSystemProperty() {
        SystemPropertyUtil.setSystemUserDir();
    }

    private static void setHadoopUserName() {
//        SystemPropertyUtil.setHadoopUserName(environmentContext.getHadoopUserName());
    }
}