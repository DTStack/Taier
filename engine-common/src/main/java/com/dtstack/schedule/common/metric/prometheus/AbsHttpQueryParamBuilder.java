package com.dtstack.schedule.common.metric.prometheus;


import com.dtstack.schedule.common.metric.Filter;
import com.dtstack.schedule.common.metric.IFunction;
import com.dtstack.schedule.common.metric.QueryInfo;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Reason:
 * Date: 2018/10/25
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class AbsHttpQueryParamBuilder {

    private static final String QUERY_TPL = "${metricName}${filter}";


    protected static String buildQuery(String metricName, QueryInfo queryInfo) throws UnsupportedEncodingException {
        String queryStr = QUERY_TPL.replace("${metricName}", metricName);
        String filterInfo;

        //generator filter
        List<Filter> filters = queryInfo.getFilters();
        List<String> filterStrList = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(filters)){
            for(Filter filter : filters){
                filterStrList.add(buildFilterStr(filter));
            }
        }

        if(CollectionUtils.isNotEmpty(filterStrList)){
            filterInfo = String.join(",", filterStrList);
            filterInfo = "{" + filterInfo + "}";
            filterInfo = URLEncoder.encode(filterInfo, Charsets.UTF_8.name());
            queryStr = queryStr.replace("${filter}", filterInfo);
        }else{
            queryStr = queryStr.replace("${filter}", "");
        }

        List<IFunction> functionList = queryInfo.getAggregator();

        for(IFunction func : functionList){
            queryStr = func.build(queryStr);
        }

        return queryStr;
    }

    private static String buildFilterStr(Filter filter) throws UnsupportedEncodingException {
        return filter.getTagk() + filter.getType() + "'" + filter.getFilter() + "'";
    }
}
