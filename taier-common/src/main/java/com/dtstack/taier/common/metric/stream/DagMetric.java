package com.dtstack.taier.common.metric.stream;

import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.stream.prometheus.DAGPrometheusMetricQuery;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-03 15:01
 * @Description:
 */
public abstract class DagMetric implements IMetric {

    private String jobId;

    private DAGPrometheusMetricQuery prometheusMetricQuery;


    public QueryInfo buildQueryInfo() {
        List<Filter> filters = Lists.newArrayList();
        Filter filter = new Filter();
        filter.setFilter(jobId);
        filter.setType("=");
        filter.setTagk("job_id");
        filters.add(filter);
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setFilters(filters);
        return queryInfo;
    }

    /**
     * 根据返回参数组装metricMap
     * @param result
     * @return
     */
    public abstract Object formatData(String result);

    public abstract Object formatData(MetricResult metricResult);

    public abstract String getMetricName();

    /**
     * 执行查询操作
     * @return
     */
    @Override
    public Object getMetric() {
        QueryInfo queryInfo = buildQueryInfo();
        String result = prometheusMetricQuery.queryResult(getMetricName(),null,queryInfo,getMetricName());
        return formatData(result);
    }

    @Override
    public String getChartName() {
        return null;
    }

}
