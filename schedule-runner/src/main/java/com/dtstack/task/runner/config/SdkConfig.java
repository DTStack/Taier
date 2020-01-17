package com.dtstack.task.runner.config;

import com.dtstack.sdk.console.client.ConsoleNotifyApiClient;
import com.dtstack.sdk.console.client.ConsoleSegmentidApiClient;
import com.dtstack.sdk.console.client.ConsoleTenantApiClient;
import com.dtstack.sdk.console.client.TaskParamApiClient;
import com.dtstack.sdk.core.common.DtInsightApi;
import com.dtstack.engine.common.env.EnvironmentContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * @author yuebai
 * @date 2019-05-22
 */
public class SdkConfig {


    @Resource
    private EnvironmentContext environmentContext;

    @Bean
    public DtInsightApi getApi() {
        if (StringUtils.isEmpty(environmentContext.getConsoleNode()) || StringUtils.isEmpty(environmentContext.getSdkToken())) {
            throw new RuntimeException("sdk 配置信息 不能为空");
        }
        String consoleNodes = "http://" + environmentContext.getConsoleNode().trim().replaceAll(",", ",http://");

        DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
                .setServerUrls(consoleNodes.split(","))
                .setEndpoint(consoleNodes)
                .setToken(environmentContext.getSdkToken().trim())
                .setSlb(true);
        return builder.buildApi();
    }

    @Bean
    public ConsoleNotifyApiClient getConsoleNotifyApiClient(DtInsightApi dtInsightApi) {
        return dtInsightApi.getSlbApiClient(ConsoleNotifyApiClient.class);
    }

    @Bean
    public TaskParamApiClient getTaskParamApiClient(DtInsightApi dtInsightApi) {
        return dtInsightApi.getSlbApiClient(TaskParamApiClient.class);
    }

    @Bean
    public ConsoleSegmentidApiClient getConsoleSegmentIdApiClient(DtInsightApi dtInsightApi) {
        return dtInsightApi.getSlbApiClient(ConsoleSegmentidApiClient.class);
    }

    @Bean
    public ConsoleTenantApiClient getConsoleTenantApiClient(DtInsightApi dtInsightApi) {
        return dtInsightApi.getSlbApiClient(ConsoleTenantApiClient.class);
    }
}
