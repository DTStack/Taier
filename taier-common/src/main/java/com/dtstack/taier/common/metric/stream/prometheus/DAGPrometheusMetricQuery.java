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

package com.dtstack.taier.common.metric.stream.prometheus;

import com.dtstack.taier.common.http.PoolHttpClient;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.prometheus.HttpQueryParamBuilder;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-11 14:51
 * @Description:
 */
public class DAGPrometheusMetricQuery {

    private static Logger logger = LoggerFactory.getLogger(DAGPrometheusMetricQuery.class);

    private PrometheusMetricQuery prometheusMetricQuery;

    private String dagQuery;

    public DAGPrometheusMetricQuery(PrometheusMetricQuery prometheusMetricQuery) {
        this.prometheusMetricQuery = prometheusMetricQuery;
    }

    public String queryResult(String metricName, Long time, QueryInfo queryInfo, String tagName) {
        String urlParam;
        try {
            urlParam = HttpQueryParamBuilder.builder(metricName, time, queryInfo);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("", e);
        }
        if(StringUtils.isEmpty(dagQuery)){
            Class clazz = prometheusMetricQuery.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field:fields){
                if("query".equals(field.getName())){
                    field.setAccessible(true);
                    try {
                        dagQuery = String.valueOf(field.get(this.prometheusMetricQuery));
                        field.setAccessible(false);
                    } catch (IllegalAccessException e) {
                        logger.error(e.getMessage(),"获取url失败");
                    }
                    break;
                }
            }
        }
        String reqUrl = dagQuery+ urlParam;
        String result = PoolHttpClient.get(reqUrl, null);
        if (result == null) {
            return null;
        }
        return result;
    }
}
