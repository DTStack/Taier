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

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.StreamMetricSupport;
import com.dtstack.taier.dao.mapper.StreamMetricSupportMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * metric support service 层
 *
 * @author ：wangchuan
 * date：Created in 上午11:39 2021/4/16
 * company: www.dtstack.com
 */
@Service
public class StreamMetricSupportService {

    @Autowired
    private StreamMetricSupportMapper streamMetricSupportDao;

    /**
     * 根据任务类型获取支持的 metric 指标的Key
     *
     * @param taskType         任务类型
     * @param componentVersion 组件版本
     * @return 支持的 metric 指标
     */
    public List<String> getMetricKeyByType(Integer taskType, String componentVersion) {
        return streamMetricSupportDao.getMetricKeyByType(taskType, componentVersion);
    }

    /**
     * 根据metric value 获取支持的metric
     *
     * @param value            metric value
     * @param componentVersion 组件版本
     * @return 支持的metric
     */
    public StreamMetricSupport getMetricByValue(String value, String componentVersion) {
        if (StringUtils.isBlank(value)) {
            throw new RdosDefineException("metric value is not null...");
        }
        StreamMetricSupport metric = streamMetricSupportDao.getMetricByValue(value, componentVersion);
        if (Objects.isNull(metric)) {
            throw new RdosDefineException(String.format("metric not found by value '%s'...", value));
        }
        return metric;
    }
}
