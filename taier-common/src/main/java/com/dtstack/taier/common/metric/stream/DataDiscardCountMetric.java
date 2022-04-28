package com.dtstack.taier.common.metric.stream;


import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.batch.BaseMetric;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据迟到累计丢弃数
 * @author jiangbo
 */
public class DataDiscardCountMetric extends StreamBaseMetric {
    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setGranularity(granularity);

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
    public String getChartName() {
        return "data_discard_count";
    }
}
