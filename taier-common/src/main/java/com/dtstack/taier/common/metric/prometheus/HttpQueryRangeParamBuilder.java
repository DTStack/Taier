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

package com.dtstack.taier.common.metric.prometheus;


import com.dtstack.taier.common.metric.QueryInfo;

import java.io.UnsupportedEncodingException;

/**
 * 根据参数构建 prometheus rangeQuery 查询的url
 * Date: 2018/10/9
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class HttpQueryRangeParamBuilder extends AbsHttpQueryParamBuilder {

    private static final String QUERY_RANGE_TPL = "query=${query}&start=${start}&end=${end}&step=${step}";

    /**
     * queryRange=flink_taskmanager_job_task_operator_KafkaConsumer_current_offsets_nbTest1_0{job_name='job10'}&start=2018-09-29T20:10:30.781Z&end=2018-09-30T20:11:00.781Z&step=30s
     */
    public static String builder(String metricName, long startTime, long endTime, QueryInfo queryInfo) throws UnsupportedEncodingException {

        long startSec = startTime / 1000;
        endTime = endTime < System.currentTimeMillis() ? endTime : System.currentTimeMillis();
        long endSec = endTime / 1000;
        String reqParam = QUERY_RANGE_TPL.replace("${start}", startSec + "").replace("${end}", endSec + "").replace("${step}", queryInfo.getGranularity());
        String queryParam = buildQuery(metricName, queryInfo);

        return reqParam.replace("${query}", queryParam);
    }
}
