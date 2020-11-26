
package com.dtstack.engine.master.config;

import com.dtstack.engine.master.router.DtArgumentCookieResolver;
import com.dtstack.engine.master.router.DtArgumentResolver;
import com.dtstack.engine.master.router.login.LoginInterceptor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/07/08
 */
@Configuration
public class MvcConfig extends DelegatingWebMvcConfiguration {

    private static final List<String> INTERCEPT_LIST;

    static {
        INTERCEPT_LIST = Lists.newArrayList("/**/getJobGraph","/**/runTimeTopOrder","/**/errorTopOrder",
                "/**/frozenTask","/**/getFillDataJobInfoPreview","/**/stopFillDataJobs",
                //队列管理
                "/node/cluster/getAllCluster","/node/console/nodeAddress","/node/console/overview","/node/console/stopAll",
                "/node/console/groupDetail",
                //资源管理
                "/node/console/clusterResources","/node/tenant/pageQuery","/node/tenant/queryTaskResourceLimits",
                "/node/console/getTaskResourceTemplate","/node/tenant/bindingQueue","/node/account/getTenantUnBandList",
                //告警通道
                "/node/account/pageQuery","/console/service/alert/page","/console/service/alert/edit",
                "/console/service/alert/setDefaultAlert","/service/alert/getByAlertId","/console/service/alert/delete",
                //多集群管理
                "/node/cluster/pageQuery","/node/component/cluster/getCluster","/node/component/getComponentVersion",
                "/node/component/addOrCheckClusterWithName","/node/component/testConnects","/node/cluster/deleteCluster");
    }

    @Autowired
    private DtArgumentResolver dtArgumentResolver;

    @Autowired
    private DtArgumentCookieResolver dtArgumentCookieResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*/*")
                .allowedMethods("*");
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new MappingJackson2HttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(dtArgumentResolver);
        argumentResolvers.add(dtArgumentCookieResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()).addPathPatterns(INTERCEPT_LIST);
        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(
                "classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }
}

