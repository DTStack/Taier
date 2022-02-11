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

package com.dtstack.taier.common.enums;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public enum EComponentType {

    FLINK(0, "Flink", "flinkConf"),
    SPARK(1, "Spark", "sparkConf"),
    HDFS(2, "HDFS", "hadoopConf"),
    YARN(3, "YARN", "yarnConf"),
    SPARK_THRIFT(4, "SparkThrift", "hiveConf"),
    HIVE_SERVER(5, "HiveServer", "hiveServerConf"),
    SFTP(6, "SFTP", "sftpConf");

    private Integer typeCode;

    private String name;

    private String confName;

    EComponentType(int typeCode, String name, String confName) {
        this.typeCode = typeCode;
        this.name = name;
        this.confName = confName;
    }

    private static final Map<Integer,EComponentType> COMPONENT_TYPE_CODE_MAP=new ConcurrentHashMap<>(16);
    private static final Map<String ,EComponentType> COMPONENT_TYPE_NAME_MAP=new ConcurrentHashMap<>(16);
    private static final Map<String ,EComponentType> COMPONENT_TYPE_CONF_NAME_MAP=new ConcurrentHashMap<>(16);
    static {
        for (EComponentType componentType : EComponentType.values()) {
            COMPONENT_TYPE_CODE_MAP.put(componentType.getTypeCode(),componentType);
            COMPONENT_TYPE_NAME_MAP.put(componentType.getName(),componentType);
            COMPONENT_TYPE_CONF_NAME_MAP.put(componentType.getConfName(),componentType);
        }
    }

    public static EComponentType getByCode(int code) {
        EComponentType componentType = COMPONENT_TYPE_CODE_MAP.get(code);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with type code:" + code);
    }

    public static EComponentType getByName(String name) {
        EComponentType componentType = COMPONENT_TYPE_NAME_MAP.get(name);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with name:" + name);
    }

    public static EComponentType getByConfName(String confName) {
        EComponentType componentType = COMPONENT_TYPE_CONF_NAME_MAP.get(confName);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with conf name:" + confName);
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public String getConfName() {
        return confName;
    }


    // 资源调度组件
    private static List<EComponentType> ResourceScheduling = Lists.newArrayList(EComponentType.YARN);

    // 存储组件
    private static List<EComponentType> StorageScheduling = Lists.newArrayList(EComponentType.HDFS);

    // 计算组件
    private static List<EComponentType> ComputeScheduling = Lists.newArrayList(
            EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER
    );

    private static List<EComponentType> CommonScheduling = Lists.newArrayList(EComponentType.SFTP);

    public static EComponentScheduleType getScheduleTypeByComponent(Integer componentCode) {
        EComponentType code = getByCode(componentCode);
        if (ComputeScheduling.contains(code)) {
            return EComponentScheduleType.COMPUTE;
        }
        if (ResourceScheduling.contains(code)) {
            return EComponentScheduleType.RESOURCE;
        }
        if (StorageScheduling.contains(code)) {
            return EComponentScheduleType.STORAGE;
        }
        if (CommonScheduling.contains(code)) {
            return EComponentScheduleType.COMMON;
        }
        throw new RdosDefineException("不支持的组件");
    }


    // hadoop引擎组件
    private static List<EComponentType> HadoopComponents = Lists.newArrayList(
            EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER,
             EComponentType.YARN
    );

    public static MultiEngineType getEngineTypeByComponent(EComponentType componentType,Integer deployType) {
        if (HadoopComponents.contains(componentType)) {
            return MultiEngineType.HADOOP;
        }
        return null;
    }

    // 需要添加TypeName的组件
    public static List<EComponentType> typeComponentVersion = Lists.newArrayList(
            EComponentType.FLINK, EComponentType.SPARK,
            EComponentType.HDFS
    );

    /**
     * 直接定位的插件的组件类型
     *
     * @param componentCode
     * @return
     */
    public static String convertPluginNameByComponent(EComponentType componentCode) {
        switch (componentCode) {
            case SFTP:
                return "dummy";
        }
        return "";
    }


    /**
     * 需要拼接  调度-存储-组件 的组件类型
     *
     * @param componentCode
     * @return
     */
    public static String convertPluginNameWithNeedVersion(EComponentType componentCode) {
        switch (componentCode) {
            case SPARK:
                return "spark";
            case FLINK:
                return "flink";
            case HDFS:
                return "hadoop";
        }
        return "";
    }

    public static List<EComponentType> notCheckComponent = Lists.newArrayList(
            EComponentType.SPARK, EComponentType.FLINK
    );

    //允许一个组件 on yarn 或 其他多种模式
    public static List<EComponentType> deployTypeComponents = Lists.newArrayList(EComponentType.FLINK);
    //允许一个组件多个版本
    public static List<EComponentType> multiVersionComponents = Lists.newArrayList(EComponentType.FLINK,EComponentType.SPARK);
    //SQL组件
    public static List<EComponentType> sqlComponent = Lists.newArrayList(
            EComponentType.SPARK_THRIFT, EComponentType.HIVE_SERVER
    );


    //没有控件渲染的组件
    public static List<EComponentType> noControlComponents = Lists.newArrayList(EComponentType.YARN,EComponentType.HDFS);

    //多hadoop版本选择组件
    public static List<EComponentType> hadoopVersionComponents = Lists.newArrayList(EComponentType.YARN,EComponentType.HDFS);

    //metadata组件
    public static List<EComponentType> metadataComponents = Lists.newArrayList(EComponentType.HIVE_SERVER,EComponentType.SPARK_THRIFT);


}

