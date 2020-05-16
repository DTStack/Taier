package com.dtstack.engine.common.client;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.config.YamlConfigParser;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.pojo.ClientTemplate;
import com.dtstack.engine.common.pojo.ComponentTestResult;
import com.dtstack.engine.common.pojo.JobResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class AbstractClient implements IClient{

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);
    public final static String PLUGIN_DEFAULT_CONFIG_NAME = "default-config.yaml";
    public final static String COMPONENT_TYPE = "componentType";

    public List<ClientTemplate> defaultPlugins;

    public AbstractClient() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(PLUGIN_DEFAULT_CONFIG_NAME);
            if (Objects.isNull(resourceAsStream)) {
                logger.info("plugin client default-config.yaml not exist!");
                return;
            }
            Map<String, Object> config = YamlConfigParser.INSTANCE.parse(resourceAsStream);
            defaultPlugins = this.convertMapTemplateToConfig(config);
            logger.info("======= plugin client============{}", defaultPlugins);
        } catch (Exception e) {
            logger.error("plugin client init default config error {}", e);
        }
    }

    @Override
	public JobResult submitJob(JobClient jobClient) {

        JobResult jobResult;
        try{
            beforeSubmitFunc(jobClient);
            jobResult = processSubmitJobWithType(jobClient);
            if (jobResult == null){
                jobResult = JobResult.createErrorResult("not support job type of " + jobClient.getJobType() + "," +
                        " you need to set it in(" + StringUtils.join(EJobType.values(),",") + ")");
            }
        }catch (Exception e){
            logger.error("", e);
            jobResult = JobResult.createErrorResult(e);
        }finally {
            afterSubmitFunc(jobClient);
        }

        return jobResult;
    }

    /**
     * job 处理具体实现的抽象
     *
     * @param jobClient 对象参数
     * @return 处理结果
     */
    protected abstract JobResult processSubmitJobWithType(JobClient jobClient);

    @Override
    public String getJobLog(JobIdentifier jobId) {
        return "";
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return false;
    }

    protected void beforeSubmitFunc(JobClient jobClient){
    }

    protected void afterSubmitFunc(JobClient jobClient){
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public List<ClientTemplate> getDefaultPluginConfig(String engineType) {
        return defaultPlugins;
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        return null;
    }

    @Override
    public List<List<Object>> executeQuery(String pluginInfo, String sql, String database) {
        return null;
    }

    @Override
    public String uploadStringToHdfs(String pluginInfo, String bytes, String hdfsPath) {
        return null;
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
            for (String key : configMap.keySet()) {
                if ("required".equalsIgnoreCase(key) || "optional".equalsIgnoreCase(key)) {
                    // 如果required 开头 单个tab选择
                    templateVos.addAll(this.getClientTemplates(configMap));
                } else {
                    Map<String, Object> groupMap = (Map<String, Object>) configMap.get(key);
                    // 不是required 开头 多个数组选择
                    ClientTemplate group = new ClientTemplate();
                    group.setValue(key);
                    group.setKey(key);
                    group.setType(EFrontType.GROUP.name());
                    group.setValues(this.getClientTemplates(groupMap));
                    templateVos.add(group);
                }
            }
            return templateVos;
        }
        return new ArrayList<>(0);
    }

    private List<ClientTemplate> getClientTemplates(Map<String, Object> configMap) {
        List<ClientTemplate> templateVos = new ArrayList<>();
        for (String key : configMap.keySet()) {
            if ("required".equalsIgnoreCase(key)) {
                Map<String, Object> value = (Map<String, Object>) configMap.get(key);
                for (String s : value.keySet()) {
                    templateVos.add(this.parseKeyValueToVo(s, value, false,true));
                }
            } else if ("optional".equalsIgnoreCase(key)) {
                Map<String, Object> value = (Map<String, Object>) configMap.get(key);
                for (String s : value.keySet()) {
                    templateVos.add(this.parseKeyValueToVo(s, value, false,false));
                }
            }
        }
        return templateVos;
    }

    private ClientTemplate parseKeyValueToVo(String valueKey, Map<String, Object> value, boolean multiValues,boolean required) {
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
                    ClientTemplate sonClientTemplate = this.parseKeyValueToVo(sonKey, sonMap, true,required);
                    sonClientTemplate.setRequired(null);
                    templateVo.setType(EFrontType.RADIO.name());
                    if (Objects.isNull(templateVo.getValues())) {
                        templateVo.setValues(new ArrayList<>());
                    }
                    templateVo.getValues().add(sonClientTemplate);
                }
            }
            if(CollectionUtils.isNotEmpty(templateVo.getValues())){
                templateVo.setValue(templateVo.getValues().get(0).getValue());
            }
        } else {
            if (defaultValue instanceof Map) {
                //依赖 radio 的选择的输入框
                Map<String, Object> defaultMap = (Map<String, Object>) defaultValue;
                templateVo.setDependencyKey(String.valueOf(defaultMap.get("dependencyKey")));
                templateVo.setDependencyValue(String.valueOf(defaultMap.get("dependencyValue")));
                templateVo.setType(EFrontType.INPUT.name());
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
