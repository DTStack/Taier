package com.dtstack.engine.entrance;

import com.dtstack.engine.common.client.config.YamlConfigParser;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.api.pojo.ClientTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.*;

/**
 * @author yuebai
 * @date 2020-05-20
 */
public class YmlParseTest extends BaseTest {

    @Test
    public void testYml() throws Exception {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("flink-template.yml");
        Map<String, Object> config = YamlConfigParser.INSTANCE.parse(resourceAsStream);
        List<ClientTemplate> clientTemplates = this.convertMapTemplateToConfig(config);
        Assert.assertNotNull(clientTemplates);
    }

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @return
     */
    protected List<ClientTemplate> convertMapTemplateToConfig(Object result) {
        if (result instanceof Map) {
            Map<String, Object> configMap = (Map<String, Object>) result;
            List<ClientTemplate> templateVos = new ArrayList<>();
            boolean hasAddRequired = false;
            for (String key : configMap.keySet()) {
                if ("required".equalsIgnoreCase(key) || "optional".equalsIgnoreCase(key)) {
                    // 如果required 开头 单个tab选择
                    if (!hasAddRequired) {
                        hasAddRequired = true;
                        templateVos.addAll(this.getClientTemplates(configMap));
                    }
                } else {
                    Object groupValue = configMap.get(key);
                    if (groupValue instanceof Map) {
                        Map<String, Object> groupMap = (Map<String, Object>) configMap.get(key);
                        String controls = (String) groupMap.get("controls");
                        ClientTemplate group = new ClientTemplate();
                        group.setKey(key);
                        Map<String, Object> groupValueMap = (Map<String, Object>) groupValue;
                        group.setDependencyKey(String.valueOf(groupValueMap.getOrDefault("dependencyKey","")));
                        group.setDependencyValue(String.valueOf(groupValueMap.getOrDefault("dependencyValue","")));
                        //控件类型
                        if (StringUtils.isNotBlank(controls)) {
                            group.setType(controls.toUpperCase());
                            Object controlsValues = groupMap.get("values");
                            if (controlsValues instanceof List) {
                                group.setValues(getControlsClientTemplates((List<String>) controlsValues));
                                if (CollectionUtils.isNotEmpty(group.getValues())) {
                                    //第一位设置为默认值
                                    group.setValue(group.getValues().get(0).getValue());
                                }
                            } else {
                                group.setValues(this.getClientTemplates(groupMap));
                            }
                        } else {
                            group.setType(EFrontType.INPUT.name());
                            group.setValues(this.getClientTemplates(groupMap));
                        }
                        templateVos.add(group);
                    }
                }
            }
            return templateVos;
        }
        return new ArrayList<>(0);
    }

    private List<ClientTemplate> getControlsClientTemplates(List<String> controlsValues) {
        List<String> controlsVals = controlsValues;
        List<ClientTemplate> templates = new ArrayList<>();
        for (Object controlsVal : controlsVals) {
            ClientTemplate clientTemplate = new ClientTemplate();
            clientTemplate.setKey(String.valueOf(controlsVal));
            clientTemplate.setValue(String.valueOf(controlsVal));
            templates.add(clientTemplate);
        }
        return templates;
    }

    private List<ClientTemplate> getClientTemplates(Map<String, Object> configMap) {
        List<ClientTemplate> templateVos = new ArrayList<>();
        for (String key : configMap.keySet()) {
            if ("required".equalsIgnoreCase(key)) {
                Map<String, Object> value = (Map<String, Object>) configMap.get(key);
                for (String s : value.keySet()) {
                    templateVos.add(this.parseKeyValueToVo(s, value, false, true));
                }
            } else if ("optional".equalsIgnoreCase(key)) {
                Map<String, Object> value = (Map<String, Object>) configMap.get(key);
                for (String s : value.keySet()) {
                    templateVos.add(this.parseKeyValueToVo(s, value, false, false));
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
                    ClientTemplate sonClientTemplate = this.parseKeyValueToVo(sonKey, sonMap, true, required);
                    sonClientTemplate.setRequired(null);
                    templateVo.setType(EFrontType.RADIO.name());
                    if (Objects.isNull(templateVo.getValues())) {
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
                if (Objects.nonNull(defaultMap.get("controls"))) {
                    //设置了控件类型
                    Object controlsValues = defaultMap.get("values");
                    templateVo.setType(String.valueOf(defaultMap.get("controls")).toUpperCase());
                    if (controlsValues instanceof List) {
                        templateVo.setValues(getControlsClientTemplates((List<String>) controlsValues));
                        if (CollectionUtils.isNotEmpty(templateVo.getValues())) {
                            //第一位设置为默认值
                            templateVo.setValue(templateVo.getValues().get(0).getValue());
                        }
                    }
                } else {
                    //依赖 radio 的选择的输入框
                    templateVo.setDependencyKey(String.valueOf(defaultMap.getOrDefault("dependencyKey","")));
                    templateVo.setDependencyValue(String.valueOf(defaultMap.getOrDefault("dependencyValue","")));
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
}