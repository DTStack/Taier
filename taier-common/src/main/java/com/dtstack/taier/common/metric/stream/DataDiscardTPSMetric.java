package com.dtstack.taier.common.metric.stream;

import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.prometheus.func.IRateFunc;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据迟到丢弃tps
 * @author jiangbo
 */
public class DataDiscardTPSMetric extends StreamBaseMetric {
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

        List<String> byLabel = Lists.newArrayList();
        byLabel.add("instance");
        byLabel.add("operator_name");
        byLabel.add("job_name");
        byLabel.add("__name__");

        IRateFunc iRateFunc = new IRateFunc();
        iRateFunc.setRangeVector("1m");
        queryInfo.addAggregator(iRateFunc);
        return queryInfo;
    }

    @Override
    public String getChartName() {
        return "data_discard_tps";
    }
}
