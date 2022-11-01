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

package com.dtstack.taier.scheduler.service;

import com.dtstack.taier.common.enums.DictType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.util.Pair;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.dao.domain.Dict;
import com.dtstack.taier.dao.mapper.ComponentConfigMapper;
import com.dtstack.taier.dao.mapper.DictMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    private ComponentConfigMapper componentConfigMapper;


    /**
     * 根据版本和组件 加载出额外的配置参数(需以自定义参数的方式)
     * yarn和hdfs的xml配置参数暂时不添加
     *
     * @param version
     * @param componentCode
     * @return
     */
    public List<ComponentConfig> loadExtraComponentConfig(String version, Integer componentCode) {
        if (StringUtils.isBlank(version) || defaultVersion.test(version)) {
            return new ArrayList<>(0);
        }
        EComponentType componentType = EComponentType.getByCode(componentCode);
        Dict extraConfig = dictMapper.getByNameValue(DictType.COMPONENT_CONFIG.type, version.trim(), null, componentType.name().toUpperCase());
        if (null == extraConfig) {
            return new ArrayList<>(0);
        }
        return componentConfigMapper.listByComponentId(Long.parseLong(extraConfig.getDictValue()), false);
    }

    public Dict getByNameAndValue(Integer dictType, String dictName, String dictValue, String dependName) {
        return dictMapper.getByNameValue(dictType, dictName, dictValue, dependName);
    }


    public List<Pair<String, List<Pair>>> groupByDependName(List<Dict> dicts) {
        Map<String, List<Dict>> versions = dicts
                .stream()
                .collect(Collectors.groupingBy(Dict::getDependName));
        List<Pair<String, List<Pair>>> groupPairs = new ArrayList<>(versions.size());

        if (versions.size() == 1 && versions.containsKey(StringUtils.EMPTY)) {
            //平铺
            List<Dict> keyDicts = versions.get(StringUtils.EMPTY);
            List templates = keyDicts.stream().map(s -> new Pair(s.getDictName(), null))
                    .collect(Collectors.toList());
            groupPairs = templates;
        } else {
            //嵌套
            for (String dependName : versions.keySet()) {
                List<Dict> keyDicts = versions.get(dependName);
                keyDicts = keyDicts.stream().sorted(Comparator.comparing(Dict::getSort)).collect(Collectors.toList());
                List<Pair> templates = keyDicts.stream().map(s -> new Pair(s.getDictName(), null))
                        .collect(Collectors.toList());

                Pair<String, List<Pair>> vendorFolder = new Pair<>(dependName, templates);
                groupPairs.add(vendorFolder);
            }
        }
        groupPairs.sort(Comparator.comparing(Pair::getKey));
        return groupPairs;
    }

    public String convertVersionNameToValue(String componentVersion, Integer taskType,  Integer deployMode) {
        if (StringUtils.isBlank(componentVersion)) {
            return "";
        }
        EScheduleJobType scheduleJobType = EScheduleJobType.getByTaskType(taskType);
        EComponentType componentType = scheduleJobType.getComponentType();
        Integer dictType = DictType.getByEComponentType(componentType);
        if (null != dictType) {
            Dict versionDict = getByNameAndValue(dictType, componentVersion.trim(), null, null);
            if (null != versionDict) {
                return versionDict.getDictValue();
            }
        }
        return "";
    }

    public List<Dict> listByDictType(DictType dictType) {
        return dictMapper.listDictByType(dictType.type);
    }

    public List<Dict> listByDictCode(String code) {
        return dictMapper.listByDictCode(code);
    }

}
