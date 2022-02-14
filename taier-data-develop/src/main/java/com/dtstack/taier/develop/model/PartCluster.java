package com.dtstack.taier.develop.model;

import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EDeployType;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.develop.model.part.PartImpl;
import com.dtstack.taier.develop.model.system.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 将集群从db初始化为PartCluster
 * part -- 组件具体的实现类 根据组件对应的组件角色 定义不同的实现方式
 * context -- 组件配置信息的context内容
 * componentModel -- 组件配置的依赖信息
 *          {
 * 	            "owner": "STORAGE", //对应组件角色 如存储
 * 	            "dependsOn": ["RESOURCE"], //组件依赖角色 如资源
 * 	            "allowCoexistence": false, //是否允许不同版本共存
 * 	            "versionDictionary": "HADOOP_VERSION" //组件版本信息字典
 * 	            "nameTemplate": "dummy" //组件对应的pluginName
 *          }
 *
 *
 * componentModelTypeConfig -- 资源组件支持的plugin列表
 *        {
 *              "YARN":"yarn2",
 *              "HDFS":{
 *              "HDFS":"hdfs2",
 *              "FLINK":[
 *                  {
 *                      "180":"yarn2-hdfs2-flink180"
 *                  },
 *                  {
 *                      "110":"yarn2-hdfs2-flink110"
 *                  },
 *                  {
 *                      "112":"yarn2-hdfs2-flink112"
 *                  }
 *               ],
 *              "SPARK":[
 *                  {
 *                      "210":"yarn2-hdfs2-spark210"
 *                  }
 *              ],
 *              "DT_SCRIPT":"yarn2-hdfs2-dtscript",
 *              "HADOOP":"yarn2-hdfs2-hadoop3",
 *              "TONY":"yarn2-hdfs2-tony",
 *              "LEARNING":"yarn2-hdfs2-learning"
 *     }
 * }
 *
 *  组件先区分为2种
 *  1. 不依赖资源组件 sftp、mysql等
 *                如果组件无版本 pluginName信息从componentModel获取
 *                如果组件有版本 pluginName信息根据版本从获取到componentModelTypeConfig获取
 *
 *  2. 依赖资源组件 如hdfs、flink等
 *                一切依赖资源组件 组件信息都需要从依赖的资源组件的componentModelTypeConfig获取

 *
 *  构建组件类型流程为 获取集群下所有组件
 *                   ｜
 *                   ｜
 *
 *  1. commonPart 直接获取componentModel 中插件名
 *  2. resourcePart 资源组件 根据组件类型 获取到componentModel判断依赖关系
 *                          根据组件版本 获取到componentModelTypeConfig     拿到组件对应的插件名
 *  3. storePart 存储组件    根据组件类型 获取到componentModel判断依赖关系
 *                          根据已经配置的资源组件 获取到componentModelTypeConfig 拿到组件对应的插件名
 *  4. standalonePart  standalone模式组件
 *                          根据组件版本 获取到componentModelTypeConfig     拿到组件对应的插件名
 *  5. singleComputePart    不依赖任务组件 但是有版本区分
 *                          根据组件版本 获取到componentModelTypeConfig     拿到组件对应的插件名
 *  6. dependComputePart    依赖资源组件 + 存储组件  且自身有可能版本
 *                          根据组件类型 获取到componentModel判断依赖关系
 *                          根据已经配置的资源组件 获取到componentModelTypeConfig
 *                          根据已经配置的存储组件 获取到componentModelTypeConfig中存储组件支持的插件列表
 *
 *                          依赖资源、存储 且pluginName依赖资源存储拼接 resource_store_compute方式   资源 + 存储 + 版本获取插件名
 *                          依赖资源、存储 但pluginName不依赖依赖资源存储拼接
 *                                       自身有版本 根据自身组件获取到componentModelTypeConfig 获取版本插件名  hive2
 *                                       自身无版本 根据自身组件获取到componentModel 获取唯一插件名   trino
 *
 *
 */
public class PartCluster {

    private final Long clusterId;
    private final Context context;
    private final DataSource dataSource;
    private Map<EComponentScheduleType, List<Component>> componentScheduleGroup;

    public PartCluster(Long clusterId, Context context, DataSource dataSource) {
        this.clusterId = clusterId;
        this.context = context;
        this.dataSource = dataSource;
        List<Component> components = this.dataSource.listAllByClusterId();
        componentScheduleGroup = initComponentScheduleGroup(components);
    }

    private Map<EComponentScheduleType, List<Component>> initComponentScheduleGroup(List<Component> components) {
        componentScheduleGroup = new HashMap<>();
        componentScheduleGroup = components.stream()
                .collect(Collectors.groupingBy(c -> context.getOwner(EComponentType.getByCode(c.getComponentTypeCode())),
                        Collectors.collectingAndThen(Collectors.toList(), component -> component)));
        List<Component> resourceComponents = componentScheduleGroup.get(EComponentScheduleType.RESOURCE);
        if (CollectionUtils.isNotEmpty(resourceComponents) && resourceComponents.size() > 1) {
            throw new IllegalArgumentException("resource component more than one");
        }
        return componentScheduleGroup;
    }

    public Part create(EComponentType componentType, String versionName, EComponentType storeType, Integer deployType) {
        EDeployType eDeployType = null;
        if (null != deployType) {
            eDeployType = EDeployType.getDeployType(deployType);
        }
        return new PartImpl(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, eDeployType);
    }
}
