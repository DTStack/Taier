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

package com.dtstack.taier.common.metric;


import com.google.common.collect.Lists;

import java.util.List;

/**
 * Reason:
 * Date: 2018/10/9
 * Company: www.dtstack.com
 * @author xuchao
 */

public class QueryInfo {

    /**查询粒度,eg: 20s*/
    private String granularity;

    /**sum,min,max,avg,stddev,stdvar,count,count_values,bottomk,topk,quantile*/
    private List<IFunction> aggregator = Lists.newArrayList();

    private List<Filter> filters;

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<IFunction> getAggregator() {
        return aggregator;
    }

    public void addAggregator(IFunction func){
        if(!func.checkParam()){
            throw new RuntimeException(String.format("this func:%s param is error..", func));
        }

        aggregator.add(func);
    }
}
