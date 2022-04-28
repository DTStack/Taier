package com.dtstack.taier.common.metric.stream;


import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.prometheus.func.CommonFunc;

import java.util.ArrayList;
import java.util.List;

/**
 * 输入RPS
 * @author jiangbo
 */
public class DataAcquisitionMetric extends StreamBaseMetric {

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

        List<String> byLabel = new ArrayList<>();
        byLabel.add("plugin_name");
        CommonFunc sumFunc = new CommonFunc("sum");
        sumFunc.setByLabel(byLabel);

        queryInfo.addAggregator(sumFunc);
        return queryInfo;
    }
}
