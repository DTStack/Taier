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


import com.dtstack.taiga.common.metric.IMetricQuery;
import com.dtstack.taiga.common.metric.MetricResult;
import com.dtstack.taiga.common.metric.QueryInfo;
import com.dtstack.taiga.pluginapi.http.PoolHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * prometheus 监控信息查询接口
 * 使用http rest api获取数据
 * Date: 2018/10/9
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class PrometheusMetricQuery implements IMetricQuery {

    private final static String QUERY_RANGE_METHOD = "/api/v1/query_range?";

    private final static String QUERY_METHOD = "/api/v1/query?";


    private String queryRange;

    private String query;


    public PrometheusMetricQuery() {
    }

    public PrometheusMetricQuery(String prometheusAddr) {
        if (!StringUtils.isEmpty(prometheusAddr)) {
            if (!prometheusAddr.startsWith("http://")) {
                prometheusAddr = "http://" + prometheusAddr.trim();
            }
        }
        this.queryRange = prometheusAddr + QUERY_RANGE_METHOD;
        this.query = prometheusAddr + QUERY_METHOD;
    }

    /**
     * 查询指定时间范围内的指标信息
     *
     * @param metricName
     * @param startTime
     * @param endTime
     * @param queryInfo
     * @param tagName
     * @return
     */
    @Override
    public MetricResult queryRange(String metricName, long startTime, long endTime, QueryInfo queryInfo, String tagName) {
        String urlParam = null;
        try {
            urlParam = HttpQueryRangeParamBuilder.builder(metricName, startTime, endTime, queryInfo);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("", e);
        }

        String reqUrl = queryRange + urlParam;
        String result = null;
        try {
            result = PoolHttpClient.get(reqUrl, null);
        } catch (IOException e) {
            return null;
        }
        if (result == null) {
            return null;
        }

        return ResultParser.parseResult(metricName, result, tagName);
    }


    /**
     * 查询指定时间点的指标
     *
     * @param metricName
     * @param time
     * @param queryInfo
     * @param tagName
     */
    public MetricResult query(String metricName, Long time, QueryInfo queryInfo, String tagName) {
        String urlParam;
        try {
            urlParam = HttpQueryParamBuilder.builder(metricName, time, queryInfo);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("", e);
        }

        String reqUrl = query + urlParam;
        String result = null;
        try {
            result = PoolHttpClient.get(reqUrl, null);
        } catch (IOException e) {
            return null;
        }
        if (result == null) {
            return null;
        }

        return ResultParser.parseResult(metricName, result, tagName);
    }
}
