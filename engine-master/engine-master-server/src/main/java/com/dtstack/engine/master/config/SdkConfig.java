package com.dtstack.engine.master.config;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.sdk.core.common.DtInsightApi;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;

/**
 * @author yuebai
 * @date 2021-04-06
 */
@Configuration
public class SdkConfig {

    @Autowired
    private EnvironmentContext context;


    @Bean(name = "dataSourceApi")
    @Conditional(DatasourceCondition.class)
    public DtInsightApi getApi() {
        String[] nodeUrls = Arrays.stream(context.getDatasourceNode().split(",")).map(node -> {
            if (!node.startsWith("http://") && !node.startsWith("https://")) {
                node = "http://" + node;
            }
            return node;
        }).toArray(String[]::new);
        DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
                .setServerUrls(nodeUrls)
                .setToken(context.getSdkToken().trim());
        return builder.buildApi();
    }


    /**
     * 数据源中心
     *
     * @param dtInsightApi
     * @return
     */
    @Bean
    @Conditional(DatasourceCondition.class)
    public DataSourceAPIClient getDataSourceClient(DtInsightApi dtInsightApi) {
        if (null == dtInsightApi) {
            return null;
        }
        return dtInsightApi.getSlbApiClient(DataSourceAPIClient.class);
    }

}

class DatasourceCondition implements Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceCondition.class);

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        boolean canBuildDatasourceApi = StringUtils.isNotBlank(conditionContext.getEnvironment().getProperty("datasource.node")) &&
                StringUtils.isNotBlank(conditionContext.getEnvironment().getProperty("sdk.token"));

        if(!canBuildDatasourceApi){
            LOGGER.info("datasource node or sdk token is null so skip init");
        }
        return canBuildDatasourceApi;
    }
}
