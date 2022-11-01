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

package com.dtstack.taier.develop.service.template;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder;
import com.dtstack.taier.develop.service.template.bulider.writer.DaWriterBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuebai
 * @date 2022/9/20
 */
@Component
public class SyncBuilderFactory implements ApplicationContextAware {

    public Map<DataSourceType, DaWriterBuilder> writerBuilderMap = new HashMap<>();
    public Map<DataSourceType, DaReaderBuilder> readBuilderMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, DaWriterBuilder> writerBuilderBeanMap = applicationContext.getBeansOfType(DaWriterBuilder.class);
        writerBuilderBeanMap.forEach((t, service) -> {
            DataSourceType dataSourceType = service.getDataSourceType();
            writerBuilderMap.put(dataSourceType, service);
        });

        Map<String, DaReaderBuilder> readBuilderBeanMap = applicationContext.getBeansOfType(DaReaderBuilder.class);
        readBuilderBeanMap.forEach((t, service) -> {
            DataSourceType dataSourceType = service.getDataSourceType();
            readBuilderMap.put(dataSourceType, service);
        });

        //针对polardb兼容
        readBuilderMap.put(DataSourceType.Polardb_For_MySQL, readBuilderMap.get(DataSourceType.MySQL));
        readBuilderMap.put(DataSourceType.SQLServer, readBuilderMap.get(DataSourceType.SQLSERVER_2017_LATER));
    }

    public DaWriterBuilder getWriterBuilder(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new RuntimeException("dataSourceType should not be null !");
        }
        DaWriterBuilder daWriterBuilder = writerBuilderMap.get(dataSourceType);
        if (daWriterBuilder == null) {
            throw new TaierDefineException(String.format("not support datasourceType %s writer ", dataSourceType.getName()));
        }
        return daWriterBuilder;
    }

    public DaReaderBuilder getReadBuilder(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new RuntimeException("dataSourceType should not be null !");
        }
        DaReaderBuilder readerBuilder = readBuilderMap.get(dataSourceType);
        if (readerBuilder == null) {
            throw new TaierDefineException(String.format("not support datasourceType %s reader ", dataSourceType.getName()));
        }
        return readerBuilder;
    }
}
