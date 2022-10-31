/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
