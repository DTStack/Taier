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

package com.dtstack.engine.config;

import com.dtstack.engine.master.impl.pojo.ClientTemplate;
import com.dtstack.engine.common.enums.EFrontType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.*;

/**
 * @author yuebai
 * @date 2020-06-23
 */
@SuppressWarnings("unchecked")
@Deprecated
public abstract class AbstractConfigParser implements IPluginConfigParser<InputStream, List<ClientTemplate>> {

    private static final String CONTROLS = "controls";
    private static final String REQUIRE = "required";
    private static final String OPTIONAL = "optional";
    private static final String VALUE = "value";
    private static final String VALUES = "values";
    private static final String DEPENDENCY_KEY = "dependencyKey";
    private static final String DEPENDENCY_VALUE = "dependencyValue";

    /**
     * 根据key值来排序
     *
     * @param clientTemplates
     * @return
     */
    public static List<ClientTemplate> sortByKey(List<ClientTemplate> clientTemplates) {
        if (CollectionUtils.isEmpty(clientTemplates)) {
            return clientTemplates;
        }
        clientTemplates.sort(Comparator.nullsFirst(Comparator.comparing(ClientTemplate::getKey, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))));
        for (ClientTemplate clientTemplate : clientTemplates) {
            AbstractConfigParser.sortByKey(clientTemplate.getValues());
        }
        return clientTemplates;
    }

    public abstract Map<String, Object> loadFile(InputStream file);

    @Override
    public List<ClientTemplate> parse(InputStream file) throws Exception {
        Map<String, Object> config = loadFile(file);
        //解析
        List<ClientTemplate> defaultPlugins = this.convertMapTemplateToConfig(config);
        //排序
        return AbstractConfigParser.sortByKey(defaultPlugins);
    }

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     * controls 用以区分控件格式
     *
     * @return
     */
    protected List<ClientTemplate> convertMapTemplateToConfig(Object result) {
        if (result instanceof Map) {
            Map<String, Object> configMap = (Map<String, Object>) result;
            List<ClientTemplate> templateVos = new ArrayList<>();
            boolean hasAddRequired = false;
            for (String key : configMap.keySet()) {
                if (REQUIRE.equalsIgnoreCase(key) || OPTIONAL.equalsIgnoreCase(key)) {
                    // 如果required 开头 单个tab选择
                    if (!hasAddRequired) {
                        hasAddRequired = true;
                        templateVos.addAll(getClientTemplates(configMap));
                    }
                } else {
                    Object groupValue = configMap.get(key);
                    if (groupValue instanceof Map) {
                        parseMapValues(configMap, templateVos, key, (Map<String, Object>) groupValue);
                    }
                }
            }
            return templateVos;
        }
        return new ArrayList<>(0);
    }

    private void parseMapValues(Map<String, Object> configMap, List<ClientTemplate> templateVos, String key, Map<String, Object> groupValue) {
        Map<String, Object> groupMap = (Map<String, Object>) configMap.get(key);
        String controls = (String) groupMap.get(CONTROLS);
        ClientTemplate group = getClientTemplateWithDependency(key, groupValue);
        //控件类型
        if (StringUtils.isNotBlank(controls)) {
            group.setType(controls.toUpperCase());
            Object controlsValues = groupMap.get(VALUES);
            if (controlsValues instanceof List) {
                //数组控件
                group.setValues(getControlsClientTemplates((List<String>) controlsValues));
                if (CollectionUtils.isNotEmpty(group.getValues())) {
                    //第一位设置为默认值
                    group.setValue(group.getValues().get(0).getValue());
                }
            } else if (EFrontType.CHECKBOX.name().equalsIgnoreCase(controls)) {
                //checkbox控件
                parseCheckBox(group, groupMap);
            } else {
                //input控件
                group.setValues(getClientTemplates(groupMap));
            }
        } else {
            group.setType(EFrontType.INPUT.name());
            group.setValues(getClientTemplates(groupMap));
        }
        templateVos.add(group);
    }

    private ClientTemplate getClientTemplateWithDependency(String key, Map<String, Object> groupValue) {
        ClientTemplate group = new ClientTemplate();
        group.setKey(key);
        group.setDependencyKey(String.valueOf(groupValue.getOrDefault(DEPENDENCY_KEY, "")));
        group.setDependencyValue(String.valueOf(groupValue.getOrDefault(DEPENDENCY_VALUE, "")));
        return group;
    }

    private void parseCheckBox(ClientTemplate group, Map<String, Object> groupMap) {
        Object checkBoxValues = groupMap.get(VALUES);
        if (checkBoxValues instanceof Map) {
            List<ClientTemplate> checkValues = new ArrayList<>();
            for (String checkKey : ((Map<String, Object>) checkBoxValues).keySet()) {
                Object checkValue = ((Map<String, Object>) checkBoxValues).get(checkKey);
                if(checkValue instanceof Map){
                    //values 对应clientTemplate
                    Map<String, Object> checkValueMap = (Map<String, Object>) checkValue;
                    ClientTemplate checkBoxTemplate = getClientTemplateWithDependency(checkKey, checkValueMap);
                    checkBoxTemplate.setType((String) checkValueMap.get(CONTROLS));
                    List<ClientTemplate> clientTemplates = getClientTemplates(checkValueMap);
                    checkBoxTemplate.setValues(clientTemplates);
                    checkValues.add(checkBoxTemplate);
                }
            }
            group.setValue(groupMap.get(VALUE));
            group.setValues(checkValues);
        }
    }

    /**
     * 解析自定义控件格式中的values
     *
     * @param controlsValues
     * @return
     */
    private List<ClientTemplate> getControlsClientTemplates(List<String> controlsValues) {
        List<ClientTemplate> templates = new ArrayList<>();
        for (Object controlsVal : controlsValues) {
            ClientTemplate clientTemplate = new ClientTemplate();
            clientTemplate.setKey(String.valueOf(controlsVal));
            clientTemplate.setValue(String.valueOf(controlsVal));
            templates.add(clientTemplate);
        }
        return templates;
    }

    /**
     * 解析必填和非必填
     *
     * @param configMap
     * @return
     */
    private List<ClientTemplate> getClientTemplates(Map<String, Object> configMap) {
        List<ClientTemplate> templateVos = new ArrayList<>();
        for (String key : configMap.keySet()) {
            Object keyMap = configMap.get(key);
            if (keyMap instanceof Map) {
                Map<String, Object> value = (Map<String, Object>) keyMap;
                for (String s : value.keySet()) {
                    templateVos.add(parseKeyValueToVo(s, value, false, REQUIRE.equalsIgnoreCase(key)));
                }
            }
        }
        return templateVos;
    }

    private ClientTemplate parseKeyValueToVo(String valueKey, Map<String, Object> value, boolean multiValues, boolean required) {
        ClientTemplate templateVo = new ClientTemplate();
        templateVo.setKey(valueKey);
        templateVo.setValue("");
        templateVo.setRequired(required);
        Object defaultValue = value.get(valueKey);
        if (defaultValue instanceof List) {
            ArrayList defaultValueList = (ArrayList) defaultValue;
            //选择框
            for (Object o : defaultValueList) {
                if (o instanceof Map) {
                    Map<String, Object> sonMap = (Map<String, Object>) o;
                    String sonKey = new ArrayList<>(sonMap.keySet()).get(0);
                    ClientTemplate sonClientTemplate = parseKeyValueToVo(sonKey, sonMap, true, required);
                    sonClientTemplate.setRequired(null);
                    templateVo.setType(EFrontType.RADIO.name());
                    if (null == templateVo.getValues()) {
                        templateVo.setValues(new ArrayList<>());
                    }
                    templateVo.getValues().add(sonClientTemplate);
                }
            }
            if (CollectionUtils.isNotEmpty(templateVo.getValues())) {
                templateVo.setValue(templateVo.getValues().get(0).getValue());
            }
        } else {
            if (defaultValue instanceof Map) {

                Map<String, Object> defaultMap = (Map<String, Object>) defaultValue;
                if (Objects.nonNull(defaultMap.get(CONTROLS))) {
                    //设置了控件类型
                    Object controlsValues = defaultMap.get(VALUES);
                    templateVo.setType(String.valueOf(defaultMap.get(CONTROLS)).toUpperCase());
                    if (EFrontType.RADIO_LINKAGE.name().equalsIgnoreCase(templateVo.getType())) {
                        templateVo.setValues(getRadioLinkage(defaultMap));
                        templateVo.setValue(defaultMap.get(VALUE));
                    } else if (controlsValues instanceof List) {
                        templateVo.setValues(getControlsClientTemplates((List<String>) controlsValues));
                        if (CollectionUtils.isNotEmpty(templateVo.getValues())) {
                            //第一位设置为默认值
                            templateVo.setValue(templateVo.getValues().get(0).getValue());
                        }
                    }
                } else {
                    //依赖 radio 的选择的输入框
                    templateVo.setDependencyKey(String.valueOf(defaultMap.getOrDefault(DEPENDENCY_KEY, "")));
                    templateVo.setDependencyValue(String.valueOf(defaultMap.getOrDefault(DEPENDENCY_VALUE, "")));
                    templateVo.setType(EFrontType.INPUT.name());
                }

            } else {
                //输入框
                templateVo.setValue(String.valueOf(Optional.ofNullable(defaultValue).orElse("")));
                if (!multiValues) {
                    templateVo.setType(EFrontType.INPUT.name());
                }
            }
        }
        return templateVo;
    }

    private List<ClientTemplate> getRadioLinkage(Map<String, Object> defaultMap) {
        Object values = defaultMap.get(VALUES);
        List<ClientTemplate> radioLinkageValues = new ArrayList<>();
        if (values instanceof Map) {
            Map<String, Object> radioLinkageMap = (Map) values;
            for (String radioKey : radioLinkageMap.keySet()) {
                Map<String, Object> radioValue = (Map<String, Object>) radioLinkageMap.get(radioKey);
                ClientTemplate radioValueTemplate = getClientTemplateWithDependency(radioKey, radioValue);
                //radio联动控件 values 只是单个
                Object linkageInputValues = radioValue.get(VALUES);
                if (linkageInputValues instanceof String) {
                    radioValueTemplate.setValues(getControlsClientTemplates(Lists.newArrayList(String.valueOf(linkageInputValues))));
                } else if (linkageInputValues instanceof Map) {
                    Map<String, Object> sonMap = (Map) ((Map<?, ?>) linkageInputValues).get(radioKey);
                    ClientTemplate sonClientTemplate = getClientTemplateWithDependency(radioKey, sonMap);
                    sonClientTemplate.setType((String)sonMap.get(CONTROLS));
                    sonClientTemplate.setValue(Optional.ofNullable(sonMap.get(VALUE)).orElse(""));
                    radioValueTemplate.setValues(Lists.newArrayList(sonClientTemplate));
                }

                radioValueTemplate.setValue(radioValue.get(VALUE));
                radioLinkageValues.add(radioValueTemplate);
            }
        }
        return radioLinkageValues;
    }
}
