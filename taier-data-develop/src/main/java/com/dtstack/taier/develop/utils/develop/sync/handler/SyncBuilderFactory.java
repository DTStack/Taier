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

package com.dtstack.taier.develop.utils.develop.sync.handler;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用于获取数据源类型对应的syncBuilder.
 *
 * @author ：wangchuan
 * @since ：Created in 上午10:20 2020/10/22
 */
@Component
public class SyncBuilderFactory {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SyncBuilderFactory.class);


    @Autowired
    private List<SyncBuilder> syncBuilders;

    private Map<Integer, SyncBuilder> syncBuilderMap = Maps.newHashMap();

    /**
     * 用于初始化map集合 syncBuilderMap
     */
    @Async
    @PostConstruct
    public void init () {
        if (CollectionUtils.isEmpty(syncBuilders)) {
            throw new RdosDefineException("List syncBuilders is empty, No SyncBuilder was found in the spring container");
        }
        syncBuilders.forEach(syncBuilder -> syncBuilderMap.put(syncBuilder.getDataSourceType().getVal(), syncBuilder));
        LOGGER.info("init SyncBuilderFactory is success...");
    }

    /**
     * 根据数据源类型获取对应的syncBuilder，数据源类型见：{@link com.dtstack.dtcenter.loader.source.DataSourceType}
     * @param dataSourceType 数据源类型
     * @return {@link SyncBuilder} 数据同步reader、writer构造类
     */
    public SyncBuilder getSyncBuilder (Integer dataSourceType) {
        SyncBuilder syncBuilder = syncBuilderMap.get(dataSourceType);
        if (Objects.isNull(syncBuilder)) {
            throw new RdosDefineException("找不到该数据源类型对应的syncBuilder...");
        }
        return syncBuilder;
    }
}
