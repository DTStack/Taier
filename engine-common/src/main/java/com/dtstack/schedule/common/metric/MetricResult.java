package com.dtstack.schedule.common.metric;

import java.util.List;

/**
 * metric 查询返回结果对象
 * Date: 2018/10/9
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MetricResult {

    private String metricName;

    private List<MetricData> metricDataList;

    public List<MetricData> getMetricDataList() {
        return metricDataList;
    }

    public void setMetricDataList(List<MetricData> metricDataList) {
        this.metricDataList = metricDataList;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
}
