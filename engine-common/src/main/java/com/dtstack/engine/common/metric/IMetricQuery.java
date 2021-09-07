package com.dtstack.engine.common.metric;

/**
 * metric 查询接口
 * Date: 2018/10/9
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface IMetricQuery {

    MetricResult queryRange(String metricName, long startTime, long endTime, QueryInfo queryInfo, String tagName);
}
