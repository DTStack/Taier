package com.dtstack.taier.common.param;


/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-11 16:26
 * @Description:
 */
public class PromtheusMetrics {

    private String status;

    private StreamMetricData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StreamMetricData getData() {
        return data;
    }

    public void setData(StreamMetricData data) {
        this.data = data;
    }
}
