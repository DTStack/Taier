package com.dtstack.schedule.common.metric;


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
