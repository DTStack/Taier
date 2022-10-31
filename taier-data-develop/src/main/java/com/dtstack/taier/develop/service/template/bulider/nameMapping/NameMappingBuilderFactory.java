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

package com.dtstack.taier.develop.service.template.bulider.nameMapping;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhiChen
 * @date 2022/1/12 11:45
 */
@Component
public class NameMappingBuilderFactory {
    private static final Logger logger = LoggerFactory.getLogger(NameMappingBuilderFactory.class);

    @Autowired
    private List<NameMappingBuilder> nameMappingBuilderList;
    private final Map<DataSourceType, NameMappingBuilder> daReaderBuilderMap = new HashMap<>();

    @PostConstruct
    private void init() {
        if (CollectionUtils.isEmpty(nameMappingBuilderList)) {
            throw new RuntimeException("no daReaderBuilderList in spring context!!");
        }
        new Thread(()-> {
            for (NameMappingBuilder nameMappingBuilder : nameMappingBuilderList) {
                daReaderBuilderMap.put(nameMappingBuilder.getDataSourceType(), nameMappingBuilder);

            }
            logger.info("init DaReaderBuilderFactory success...");
        }, "DaReaderBuilderFactoryInitThread").start();
    }



    public NameMappingBuilder getDaReaderBuilder(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new RuntimeException("dataSourceType should not be null !");
        }

        NameMappingBuilder nameMappingBuilder = daReaderBuilderMap.get(dataSourceType);
        //目前不是必配置项
//        if (nameMappingBuilder ==null) {
//            throw new RdosDefineException("暂不支持该源端");
//        }
        return nameMappingBuilder;
    }
}
