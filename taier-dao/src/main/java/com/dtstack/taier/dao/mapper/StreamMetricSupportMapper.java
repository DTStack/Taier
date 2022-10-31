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

package com.dtstack.taier.dao.mapper;

import com.dtstack.taier.dao.domain.StreamMetricSupport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 各任务支持的 metric 指标
 *
 * @author ：wangchuan
 * date：Created in 上午11:33 2021/4/16
 * company: www.dtstack.com
 */
@Mapper
public interface StreamMetricSupportMapper {

    /**
     * 根据任务类型获取支持的 metric 指标 key
     *
     * @param taskType         任务类型
     * @param componentVersion 组件版本
     * @return 支持的 metric 指标
     */
    List<String> getMetricKeyByType(@Param("taskType") Integer taskType, @Param("componentVersion") String componentVersion);

    /**
     * 根据任务类型获取支持的 metric 指标 集合
     *
     * @param taskType         任务类型
     * @param componentVersion 组件版本
     * @return 支持的 metric 指标
     */
    List<StreamMetricSupport> getMetricByType(@Param("taskType") Integer taskType, @Param("componentVersion") String componentVersion);

    /**
     * 根据metric value获取 metric
     *
     * @param value            指标value
     * @param componentVersion 组件版本
     * @return metric 指标
     */
    StreamMetricSupport getMetricByValue(@Param("value") String value, @Param("componentVersion") String componentVersion);
}
