package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.cache.DictCache;
import com.dtstack.engine.master.enums.DictType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
    private ScheduleDictDao scheduleDictDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    /**
     * 获取hadoop 和 flink spark组件的版本
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
        ScheduleDict extraConfig = scheduleDictDao.getByNameValue(DictType.COMPONENT_CONFIG.type, version.trim(), null, componentType.name().toUpperCase());
        if (null == extraConfig) {
            return new ArrayList<>(0);
        }
        return componentConfigDao.listByComponentId(Long.parseLong(extraConfig.getDictValue()), false);
    }

    public ScheduleDict getTypeDefaultValue(Integer type) {
        return scheduleDictDao.getTypeDefault(type);
    }

    public ScheduleDict getByNameAndValue(Integer dictType,String dictName,String dictValue,String dependName){
        return scheduleDictDao.getByNameValue(dictType, dictName, dictValue,dependName);
    }

    private List<ClientTemplate> getNormalVersion(Integer type) {
        List<ScheduleDict> normalVersionDict = scheduleDictDao.listDictByType(type);
        if (CollectionUtils.isEmpty(normalVersionDict)) {
            return new ArrayList<>(0);
        }
        return normalVersionDict
                .stream()
                .map(s -> new ClientTemplate(s.getDictName(), s.getDictValue()))
                .collect(Collectors.toList());
    }

    private List<ClientTemplate> getHadoopVersion() {
        List<ScheduleDict> scheduleDicts = scheduleDictDao.listDictByType(DictType.HADOOP_VERSION.type);
        Map<String, List<ScheduleDict>> versions = scheduleDicts
                .stream()
                .collect(Collectors.groupingBy(ScheduleDict::getDictCode));
        List<ClientTemplate> clientTemplates = new ArrayList<>(versions.size());
        for (String key : versions.keySet()) {
            List<ScheduleDict> keyDicts = versions.get(key);
            Map<String, List<ScheduleDict>> dependName = keyDicts
                    .stream()
                    .collect(Collectors.groupingBy(s -> Optional.ofNullable(s.getDependName()).orElse("")));
            for (ScheduleDict keyDict : keyDicts) {
                //最外部
                if (StringUtils.isBlank(keyDict.getDependName())) {
                    ClientTemplate clientTemplate = new ClientTemplate(keyDict.getDictName(), keyDict.getDictValue());
                    List<ScheduleDict> dependDict = dependName.get(keyDict.getDictName());
                    if (!CollectionUtils.isEmpty(dependDict)) {
                        clientTemplate.setValues(dependDict
                                .stream()
                                .map(s -> new ClientTemplate(s.getDictName(), s.getDictValue()))
                                .collect(Collectors.toList()));
                    }
                    clientTemplates.add(clientTemplate);
                }
            }
        }
        return clientTemplates;
    }


    public List<ScheduleDict> listById(Long id, Integer size) {
        if (id == null) {
            id = 0L;
        }

        if (size == null) {
            size = DictCache.size;
        }

        return scheduleDictDao.listById(id,size);
    }

    public ScheduleDict getByNameAndCodeAndDependName(String dictCode, String dictName, String dependName) {
        return scheduleDictDao.getByNameAndCodeAndDependName(dictCode,dictName,dependName);
    }
}
