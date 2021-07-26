package com.dtstack.engine.remote.akka.config;

import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Router;
import akka.routing.RoutingLogic;
import com.dtstack.engine.remote.akka.constant.AkkaConfigConstant;
import com.dtstack.engine.remote.akka.constant.GlobalConstant;
import com.dtstack.engine.remote.enums.RouterTypeEnum;
import com.typesafe.config.Config;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: dazhi
 * @Date: 2020/9/1 4:27 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AkkaConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaConfig.class);
    private static Config config;
    private static Environment environment;
    private static ApplicationContext applicationContext;
    private static final AtomicBoolean load = new AtomicBoolean(Boolean.FALSE);

    public static void init(Environment environment, ApplicationContext applicationContext) {
        AkkaConfig.applicationContext = applicationContext;
        AkkaConfig.environment = environment;
        init();
        load.set(Boolean.TRUE);
    }

    public static void init() {
        String property = environment.getProperty(AkkaConfigConstant.CONFIG_PATH);
        if (StringUtils.isBlank(property)) {
            // 不配置配置文件，默认{user.dir}/conf下文件 application-common.properties 和 application.properties
            AkkaLoad.load(environment.getProperty(GlobalConstant.BASE_PATH));
            return;
        }

        AkkaLoad.load(property);
    }

    public static Boolean hasLoad(){
        return load.get();
    }

    public static Config getConfig() {
        return config;
    }

    public static Router getRouter(){
        // 判断是否使用自定义路由器
        String clusterRouterCustomize = AkkaConfigConstant.CLUSTER_ROUTER_CUSTOMIZE;
        String routersCustomize = getValueWithDefault(clusterRouterCustomize,"");

        if (StringUtils.isNotBlank(routersCustomize)) {
            try {
                Class<?> aClass = Class.forName(routersCustomize);

                if (aClass.isAssignableFrom(RoutingLogic.class)) {
                    return new Router((RoutingLogic)aClass.newInstance());
                }
            } catch (Exception e) {
                LOGGER.info("customize load failure... use default route roundRobin, exception:e:{}",Arrays.toString(e.getStackTrace()));
            }
        }

        // 使用默认的路由器
        String keyName = AkkaConfigConstant.CLUSTER_ROUTER;
        String routers = getValueWithDefault(keyName, RouterTypeEnum.ROUND_ROBIN_ROUTING.getType());
        RoutingLogic routingLogic = RouterTypeEnum.getRoutingLogicByType(routers);
        if (routingLogic==null) {
            routingLogic = new RoundRobinRoutingLogic();
        }
        return new Router(routingLogic);
    }

    public static Set<String> getLocalRoles() {
        Set<String> roles = new HashSet<>();
        String keyName = AkkaConfigConstant.ROLE_NAME;

        if (config.hasPath(keyName)) {
            List<String> stringList = config.getStringList(keyName);
            if (CollectionUtils.isNotEmpty(stringList)) {
                roles.addAll(stringList);
            }
        } else {
            String property = environment.getProperty(keyName);
            if (StringUtils.isNotBlank(property)) {
                String[] split = property.split(",");
                roles.addAll(Arrays.asList(split));
            }
        }
        return roles;
    }

    public static Long getAkkaAskResultTimeout() {
        String keyName = AkkaConfigConstant.AKKA_ASK_RESULTTIMEOUT;
        return Long.valueOf(getValueWithDefault(keyName, "20"));
    }

    public static Long getAkkaAskTimeout() {
        String keyName = AkkaConfigConstant.AKKA_ASK_TIMEOUT;
        return Long.valueOf(getValueWithDefault(keyName, "120"));
    }

    public static Integer getHandlerNumber() {
        String keyName = AkkaConfigConstant.AKKA_ACTOR_HANDLER_NUMBER;
        return Integer.valueOf(getValueWithDefault(keyName, "20"));
    }

    private static String getValueWithDefault(String configKey, String defaultValue) {
        String configValue = null;
        if (config.hasPath(configKey)) {
            configValue = config.getString(configKey);
        } else {
            configValue = environment.getProperty(configKey);
        }
        if (StringUtils.isBlank(configValue)) {
            return defaultValue;
        } else {
            return configValue;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
