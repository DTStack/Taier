package com.dtstack.taier.common.metric.stream;


import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.QueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务失败历史
 *
 * @author ：wangchuan
 * date：Created in 下午5:33 2021/6/7
 * company: www.dtstack.com
 */
public class FailOverHistoryMetric extends StreamBaseMetric {

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setGranularity(granularity);
        Filter filter = new Filter();
        filter.setType("=");
        filter.setTagk("job_id");
        filter.setFilter(jobId);
        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);
        queryInfo.setFilters(filterList);
        return queryInfo;
    }

}
