package com.dtstack.taier.common.metric.stream;

import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.prometheus.func.CommonFunc;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 各数据源输入bps
 * @author jiangbo
 */
public class SourceMetric extends StreamBaseMetric {

    @Override
    public String getTagName() {
        return "operator_name";
    }

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setGranularity(granularity);
        Filter filter = new Filter();
        filter.setType("=");
        filter.setFilter(jobId);
        filter.setTagk("job_id");

        List<Filter> filterList = new ArrayList();
        filterList.add(filter);

        queryInfo.setFilters(filterList);

        List<String> byLabel = Lists.newArrayList();
        byLabel.add("instance");
        byLabel.add("job");
        byLabel.add("operator_name");
        byLabel.add("job_name");
        byLabel.add("__name__");

        CommonFunc sumFunc = new CommonFunc("sum");
        sumFunc.setByLabel(byLabel);

        queryInfo.addAggregator(sumFunc);
        return queryInfo;
    }
}
