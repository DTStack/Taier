package com.dtstack.taier.common.metric.stream.prometheus;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.http.PoolHttpClient;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.prometheus.HttpQueryRangeParamBuilder;


/**
 * 自定义 prometheus 查询器
 *
 * @author ：wangchuan
 * date：Created in 下午2:16 2021/4/16
 * company: www.dtstack.com
 */
public class CustomPrometheusMetricQuery<T> implements ICustomMetricQuery<T> {

    private final static String QUERY_RANGE_METHOD = "/api/v1/query_range?";

    private final static String HTTP_PREFIX = "http://";

    private final String queryRange;

    public CustomPrometheusMetricQuery(String prometheusAddr) {
        if (prometheusAddr.trim().startsWith(HTTP_PREFIX)) {
            this.queryRange = prometheusAddr.trim() + QUERY_RANGE_METHOD;
        } else {
            this.queryRange = HTTP_PREFIX + prometheusAddr.trim() + QUERY_RANGE_METHOD;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T queryRange(String metricName, Long startTime, Long endTime, QueryInfo queryInfo, Integer maxPoint) {
        String urlParam;
        try {
            urlParam = HttpQueryRangeParamBuilder.builder(metricName, startTime, endTime, queryInfo);
        } catch (Exception e) {
            throw new RdosDefineException("failed to build prometheus request param...", e);
        }
        String reqUrl = queryRange + urlParam;
        String result = PoolHttpClient.get(reqUrl, null);
        if (result == null) {
            return null;
        }
        return (T) CustomResultParser.parseResult(result, maxPoint);
    }
}
