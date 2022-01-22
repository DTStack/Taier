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

package com.dtstack.taiga.common.metric.prometheus;


import com.dtstack.taiga.common.metric.Filter;
import com.dtstack.taiga.common.metric.IFunction;
import com.dtstack.taiga.common.metric.QueryInfo;
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
