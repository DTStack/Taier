package com.dtstack.taier.common.metric.stream;

import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.MetricData;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.Tuple;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 14:17 2019-07-12
 * @Description：checkpoint 个数 普罗米修斯
 */
public class CheckPointsNumberMetric extends DataDelayMetric {
    private String metricName;

    public CheckPointsNumberMetric(String jobId, PrometheusMetricQuery prometheusMetricQuery) {
        super(jobId, prometheusMetricQuery);
    }

    public CheckPointsNumberMetric(String jobId, PrometheusMetricQuery prometheusMetricQuery, String metricName) {
        super(jobId, prometheusMetricQuery);
        this.metricName = metricName;
    }

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();
        Filter filter = new Filter();
        filter.setType("=");
        filter.setTagk("job_id");
        filter.setFilter(jobId);

        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);

        queryInfo.setFilters(filterList);
        return queryInfo;
    }

    @Override
    public Object formatData(MetricResult metricResult) {
        Integer count = 0;
        if(metricResult != null && CollectionUtils.isNotEmpty(metricResult.getMetricDataList())){
            for (MetricData metricData : metricResult.getMetricDataList()) {
                for (Object dp : metricData.getDps()) {
                    count = ((Tuple<Long, Double>)dp).getTwo().intValue();
                }
            }
        }

        return count;
    }

    @Override
    public String getTagName() {
        return null;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    @Override
    public String getMetricName() {
        return metricName;
    }
}
