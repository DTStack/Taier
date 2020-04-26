package com.dtstack.schedule.common.metric.batch;


import com.dtstack.schedule.common.metric.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:24 2019-07-16
 * @Description：
 */
public class SyncJobMetricWithCountMaxSum extends BaseMetric {

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();

        Filter filter = new Filter();
        filter.setType("=");
        filter.setFilter(jobId);
        filter.setTagk("job_id");

        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);

        queryInfo.setFilters(filterList);

        return queryInfo;
    }

    @Override
    protected Long formatData(MetricResult metricResult) {
        long sumCount = 0L;
        if (metricResult != null && CollectionUtils.isNotEmpty(metricResult.getMetricDataList())) {
            for (MetricData metricData : metricResult.getMetricDataList()) {
                long count = 0L;
                if (CollectionUtils.isNotEmpty(metricData.getDps())){
                    Object dp = metricData.getDps().get(metricData.getDps().size()-1);
                    count = Math.max(count, ((Tuple<Long, Double>) dp).getTwo().longValue());
                }
                sumCount += count;
            }
        }
        return sumCount;
    }

    @Override
    public String getTagName() {
        return "subtask_index";
    }
}
