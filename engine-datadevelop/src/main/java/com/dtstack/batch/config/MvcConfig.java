

package com.dtstack.batch.config;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.router.DtArgumentCookieResolver;
import com.dtstack.engine.master.router.DtArgumentParamOrHeaderResolver;
import com.dtstack.engine.master.router.DtArgumentResolver;
import com.dtstack.engine.master.router.login.LoginInterceptor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.ArrayList;
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
                "/node/component/addOrCheckClusterWithName","/node/component/testConnects","/node/cluster/deleteCluster",
                // 安全审计
                "/node/securityAudit/pageQuery","/node/securityAudit/getOperationList",
                // 告警
                "/node/alert/edit","/node/alert/setDefaultAlert","/node/alert/page","/node/alert/getByAlertId","/node/alert/delete"
                ,"/node/alert/list/show","/node/alert/testAlert","/node/status"
                );
    }

    @Autowired
    private DtArgumentResolver dtArgumentResolver;

    @Autowired
    private DtArgumentCookieResolver dtArgumentCookieResolver;

    @Autowired
    private DtArgumentParamOrHeaderResolver dtArgumentParamOrHeaderResolver;

    @Autowired
    private EnvironmentContext environmentContext;

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
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);
            converters.add(0, mappingJackson2HttpMessageConverter);
        super.configureMessageConverters(converters);
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(dtArgumentResolver);
        argumentResolvers.add(dtArgumentCookieResolver);
        argumentResolvers.add(dtArgumentParamOrHeaderResolver);
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

