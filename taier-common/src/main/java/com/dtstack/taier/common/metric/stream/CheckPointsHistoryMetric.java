package com.dtstack.taier.common.metric.stream;


import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.QueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 14:17 2019-07-12
 * @Description：checkpoint 历史记录 普罗米修斯
 */
public class CheckPointsHistoryMetric extends StreamBaseMetric {

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setGranularity("60s");
        Filter filter = new Filter();
        filter.setType("=");
        filter.setTagk("job_id");
        filter.setFilter(jobId);
        List<Filter> filterList = new ArrayList();
        filterList.add(filter);

        queryInfo.setFilters(filterList);

        return queryInfo;
    }

}
