package com.dtstack.taier.common.metric.batch;


import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.prometheus.func.CommonFunc;

import java.util.ArrayList;
import java.util.List;

public class SyncJobMetric extends BaseMetric {

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

        CommonFunc func = new CommonFunc("max");
        queryInfo.addAggregator(func);
        return queryInfo;
    }

}
