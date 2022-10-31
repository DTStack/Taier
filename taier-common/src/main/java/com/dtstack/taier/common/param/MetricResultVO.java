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

package com.dtstack.taier.common.param;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * metric 指标详情信息
 *
 * @author ：wangchuan
 * date：Created in 上午11:55 2021/4/16
 * company: www.dtstack.com
 */
public class MetricResultVO {

    @ApiModelProperty(value = "指标相关信息", example = "{\"host\":\"127.0.0.1\"}")
    private Map<String, String> metric;

    @ApiModelProperty(value = "时间-指标集合", example = "{\"host\":\"127.0.0.1\"}")
    private List<MetricValueVO> values;

    public Map<String, String> getMetric() {
        return metric;
    }

    public List<MetricValueVO> getValues() {
        return values;
    }

    public void setMetric(Map<String, String> metric) {
        this.metric = metric;
    }

    public void setValues(List<MetricValueVO> values) {
        this.values = values;
    }
}
