package com.dtstack.schedule.common.metric.prometheus;

/**
 * Reason:
 * Date: 2018/10/9
 * Company: www.dtstack.com
 * @author xuchao
 */

public class PrometheusConfigure {

    private String host;

    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
