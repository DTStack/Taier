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

package com.dtstack.taiga.scheduler.service;

import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.enums.EScheduleJobType;
import com.dtstack.taiga.common.env.EnvironmentContext;
import com.dtstack.taiga.dao.domain.ComponentConfig;
import com.dtstack.taiga.dao.domain.ScheduleDict;
import com.dtstack.taiga.dao.mapper.ComponentConfigMapper;
import com.dtstack.taiga.dao.mapper.DictMapper;
import com.dtstack.taiga.scheduler.enums.DictType;
import com.dtstack.taiga.scheduler.impl.pojo.ClientTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuebai
 * @date 2021-03-02
 */
@Component
public class ScheduleDictService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleDictService.class);
    public static final Predicate<String> defaultVersion = version -> "hadoop2".equalsIgnoreCase(version) || "hadoop3".equalsIgnoreCase(version)
            || "Hadoop 2.x".equalsIgnoreCase(version) || "Hadoop 3.x".equalsIgnoreCase(version);

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentConfigMapper componentConfigMapper;

    /**
     * 获取hadoop 和 flink spark组件的版本(有版本选择的才会在这获取)
     *
     * @return
     */
    public Map<String, List<ClientTemplate>> getVersion() {
        Map<String, List<ClientTemplate>> versions = new HashMap<>(8);
        versions.put("hadoopVersion", getHadoopVersion());
        versions.put(EComponentType.FLINK.getName(), getNormalVersion(DictType.FLINK_VERSION.type));
        versions.put(EComponentType.SPARK_THRIFT.getName(), getNormalVersion(DictType.SPARK_THRIFT_VERSION.type));
        versions.put(EComponentType.SPARK.getName(), getNormalVersion(DictType.SPARK_VERSION.type));
        versions.put(EComponentType.HIVE_SERVER.getName(), getNormalVersion(DictType.HIVE_VERSION.type));
        return versions;
    }


    /**
     * 根据版本和组件 加载出额外的配置参数(需以自定义参数的方式)
     * yarn和hdfs的xml配置参数暂时不添加
     *
     * @param version
     * @param componentCode
     * @return
     */
    public List<ComponentConfig> loadExtraComponentConfig(String version, Integer componentCode) {
        if (StringUtils.isBlank(version) || defaultVersion.test(version) || !environmentContext.isCanAddExtraConfig()) {
            return new ArrayList<>(0);
        }
        EComponentType componentType = EComponentType.getByCode(componentCode);
        ScheduleDict extraConfig = dictMapper.getByNameValue(DictType.COMPONENT_CONFIG.type, version.trim(), null, componentType.name().toUpperCase());
        if (null == extraConfig) {
            return new ArrayList<>(0);
        }
        return componentConfigMapper.listByComponentId(Long.parseLong(extraConfig.getDictValue()), false);
    }

    public ScheduleDict getTypeDefaultValue(Integer type) {
        return dictMapper.getTypeDefault(type);
    }

    public ScheduleDict getByNameAndValue(Integer dictType, String dictName, String dictValue, String dependName) {
        return dictMapper.getByNameValue(dictType, dictName, dictValue, dependName);
    }

    private List<ClientTemplate> getNormalVersion(Integer type) {
        List<ScheduleDict> normalVersionDict = dictMapper.listDictByType(type);
        if (CollectionUtils.isEmpty(normalVersionDict)) {
            return new ArrayList<>(0);
        }

        return normalVersionDict
                .stream()
                .map(s -> {
                    ClientTemplate clientTemplate = new ClientTemplate(s.getDictName(), s.getDictValue());
                    if (DictType.FLINK_VERSION.type.equals(s.getType()) && StringUtils.isNotBlank(s.getDependName())) {
                        List<Integer> collect = Stream.of(s.getDependName().split(",")).mapToInt(Integer::parseInt)
                                .boxed().collect(Collectors.toList());
                        clientTemplate.setDeployTypes(collect);
                    }
                    return clientTemplate;
                })
                .collect(Collectors.toList());
    }

    private List<ClientTemplate> getHadoopVersion() {
        List<ScheduleDict> scheduleDicts = dictMapper.listDictByType(DictType.HADOOP_VERSION.type);
        Map<String, List<ScheduleDict>> versions = scheduleDicts
                .stream()
                .collect(Collectors.groupingBy(ScheduleDict::getDependName));
        List<ClientTemplate> clientTemplates = new ArrayList<>(versions.size());
        for (String dependName : versions.keySet()) {
            List<ScheduleDict> keyDicts = versions.get(dependName);
            keyDicts = keyDicts.stream().sorted(Comparator.comparing(ScheduleDict::getSort)).collect(Collectors.toList());
            List<ClientTemplate> templates = keyDicts.stream().map(s -> new ClientTemplate(s.getDictName(), s.getDictValue()))
                    .collect(Collectors.toList());
            ClientTemplate vendorFolder = new ClientTemplate();
            vendorFolder.setKey(dependName);
            vendorFolder.setValues(templates);
            clientTemplates.add(vendorFolder);
        }
        clientTemplates.sort(Comparator.comparing(ClientTemplate::getKey));
        return clientTemplates;
    }

    public String convertVersionNameToValue(String componentVersion, Integer taskType) {
        if (StringUtils.isBlank(componentVersion)) {
            return "";
        }
        EScheduleJobType scheduleJobType = EScheduleJobType.getByTaskType(taskType);
        EComponentType componentType = scheduleJobType.getComponentType();
        Integer dictType = DictType.getByEComponentType(componentType);
        if (null != dictType) {
            ScheduleDict versionDict = getByNameAndValue(dictType, componentVersion.trim(), null, null);
            if (null != versionDict) {
                return versionDict.getDictValue();
            }
        }
        return "";
    }

    public List<ScheduleDict> listByDictType(DictType dictType) {
        return dictMapper.listDictByType(dictType.type);
    }

}
