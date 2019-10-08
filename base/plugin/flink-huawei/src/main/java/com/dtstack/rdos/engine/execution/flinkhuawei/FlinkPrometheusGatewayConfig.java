package com.dtstack.rdos.engine.execution.flinkhuawei;

/**
 * prometheus gateway 配置信息
 * Date: 2018/10/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkPrometheusGatewayConfig {

    public static final String DEFAULT_GATEWAY_CLASS = "org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter";

    public static final String PROMGATEWAY_CLASS_KEY = "metrics.reporter.promgateway.class";

    public static final String PROMGATEWAY_HOST_KEY = "metrics.reporter.promgateway.host";

    public static final String PROMGATEWAY_PORT_KEY = "metrics.reporter.promgateway.port";

    public static final String PROMGATEWAY_JOBNAME_KEY = "metrics.reporter.promgateway.jobName";

    public static final String PROMGATEWAY_RANDOMJOBNAMESUFFIX_KEY = "metrics.reporter.promgateway.randomJobNameSuffix";

    public static final String PROMGATEWAY_DELETEONSHUTDOWN_KEY = "metrics.reporter.promgateway.deleteOnShutdown";

    private String reporterClass;

    private String deleteOnShutdown;

    private String gatewayHost;

    private String gatewayJobName;

    private String randomJobNameSuffix;

    private String gatewayPort;


    public String getReporterClass() {
        return reporterClass;
    }

    public void setReporterClass(String reporterClass) {
        this.reporterClass = reporterClass;
    }

    public String getDeleteOnShutdown() {
        return deleteOnShutdown;
    }

    public void setDeleteOnShutdown(String deleteOnShutdown) {
        this.deleteOnShutdown = deleteOnShutdown;
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    public String getGatewayJobName() {
        return gatewayJobName;
    }

    public void setGatewayJobName(String gatewayJobName) {
        this.gatewayJobName = gatewayJobName;
    }

    public String getRandomJobNameSuffix() {
        return randomJobNameSuffix;
    }

    public void setRandomJobNameSuffix(String randomJobNameSuffix) {
        this.randomJobNameSuffix = randomJobNameSuffix;
    }

    public String getGatewayPort() {
        return gatewayPort;
    }

    public void setGatewayPort(String gatewayPort) {
        this.gatewayPort = gatewayPort;
    }
}
