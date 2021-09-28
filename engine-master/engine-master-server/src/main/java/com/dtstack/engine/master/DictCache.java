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

package com.dtstack.engine.master;

import com.dtstack.engine.domain.ScheduleDict;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Auther: dazhi
 * @Date: 2021/6/18 3:36 下午
 * @Email:dazhi@dtstack.com
 * @Description: 缓存字典表
 */
@Component
public class DictCache implements InitializingBean {

    @Autowired
    private ScheduleDictService scheduleDictService;

    private final Cache<String, List<String>> hadoopVersionCache = CacheBuilder.newBuilder().initialCapacity(1000).build();

    private final String HADOOP_VERSION = "hadoop_version";
    private final List<String> HADOOP_VERSION_ARRAY = Lists.newArrayList("Hadoop2","Hadoop3");
    public static final Integer size = 500;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() throws ExecutionException {
        Long id = 0L;
        List<ScheduleDict> scheduleDicts = scheduleDictService.listById(id, size);
        for (String version : HADOOP_VERSION_ARRAY) {
            hadoopVersionCache.put(version, Lists.newArrayList());
        }
        while (CollectionUtils.isNotEmpty(scheduleDicts)) {

            for (ScheduleDict scheduleDict : scheduleDicts) {
                String dictCode = scheduleDict.getDictCode();

                if (HADOOP_VERSION.equals(dictCode)) {

                    String dictName = scheduleDict.getDictName();
                    if (StringUtils.isNotBlank(dictName) && HADOOP_VERSION_ARRAY.contains(dictName)) {
                        List<String> values = hadoopVersionCache.get(dictName, Lists::newArrayList);
                        values.add(scheduleDict.getDictValue());
                        hadoopVersionCache.put(dictName,values);
                        continue;
                    }

                    String dependName = scheduleDict.getDependName();
                    if (StringUtils.isNotBlank(dependName) && HADOOP_VERSION_ARRAY.contains(dependName)) {
                        List<String> values = hadoopVersionCache.get(dependName, Lists::newArrayList);
                        values.add(scheduleDict.getDictValue());
                        hadoopVersionCache.put(dependName,values);
                    }
                }

                id = scheduleDict.getId();
            }

            scheduleDicts = scheduleDictService.listById(id, size);
        }
    }

    public List<String> getHadoopVersion(String version) {
        try {
            return hadoopVersionCache.get(version, Lists::newArrayList);
        } catch (ExecutionException e) {
            return Lists.newArrayList();
        }
    }





}
