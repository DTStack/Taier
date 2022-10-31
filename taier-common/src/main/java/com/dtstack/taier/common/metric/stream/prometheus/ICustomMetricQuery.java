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


import com.dtstack.taier.common.metric.QueryInfo;

/**
 * 自定义 metric 查询接口
 *
 * @author ：wangchuan
 * date：Created in 下午2:26 2021/4/16
 * company: www.dtstack.com
 */
public interface ICustomMetricQuery<T> {

    /**
     * 查询指定范围的指标
     *
     * @param metricName prometheus 指标名称
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param queryInfo  查询过滤信息
     * @param maxPoint   返回点位最大数量
     * @return 指标信息
     */
    T queryRange(String metricName, Long startTime, Long endTime, QueryInfo queryInfo, Integer maxPoint);

}
