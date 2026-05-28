package com.dtstack.taier.scheduler.monitor;


import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.metrics.monitor.TpMonitor;
import com.dtstack.taier.metrics.prometheus.CollectorRegistryHolder;
import com.dtstack.taier.metrics.prometheus.PrometheusPushGatewayManager;
import io.prometheus.client.exporter.PushGateway;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * Service with monitor with {@link java.util.concurrent.ScheduledThreadPoolExecutor}
 * @author xingyi
 * @date 2025/9/17
 */
@Service
public class ScheduleTpMonitor
        implements
        InitializingBean,
        DisposableBean,
        ApplicationListener<ApplicationStartedEvent> {

    private final Logger LOGGER = LoggerFactory.getLogger(ScheduleTpMonitor.class);

    @Resource
    private EnvironmentContext environmentContext;

    private TpMonitor tpMonitor;

    public void interval() {

        long monitorInterval = environmentContext.getMonitorMetricsInterval();
        long monitorDelay = environmentContext.getMonitorMetricsDelay();
        tpMonitor.interval(monitorInterval, monitorDelay,
                environmentContext.getSupportMonitorMetricsType());
    }

    @Override
    public void destroy() {
        tpMonitor.destroy();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // @see CollectorTypeEnum配置为Micrometer ，需要实现对应的MicroMeterCollector
        if (environmentContext.getIsMonitorMetrics()) {
            interval();
        }
        if (StringUtils.isNotEmpty(environmentContext.getPrometheusPushGatewayUrl())) {

            String prometheusPushGatewayUrl = environmentContext.getPrometheusPushGatewayUrl();
            PushGateway pushGateway = initializePushGateway(prometheusPushGatewayUrl);

            // register metrics to prometheus push gateway
            new PrometheusPushGatewayManager(pushGateway,
                    CollectorRegistryHolder.getCollectorRegistry(),
                    environmentContext.getPrometheusPushGatewayInterval(), "schedule_tp", null,
                    PrometheusPushGatewayManager.ShutdownOperation.NONE);

        }
    }

    private PushGateway initializePushGateway(String url) {
        try {
            return new PushGateway(new URL(url));
        } catch (MalformedURLException ex) {
            LOGGER.error("Invalid PushGateway base url '{}': update your configuration to a valid URL", url);
            return new PushGateway(url);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        tpMonitor = new TpMonitor();
    }
}
