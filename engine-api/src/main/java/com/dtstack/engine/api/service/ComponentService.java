package com.dtstack.engine.api.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentVO;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ComponentService {
    /**
     * {
     * "1":{
     * "xx":"xx"
     * }
     * }
     */
    public String listConfigOfComponents(@Param("tenantId") Long dtUicTenantId, @Param("engineType") Integer engineType);

    public Component getOne(@Param("id") Long id);

    @Forbidden
    public String getSftpClusterKey(Long clusterId);

    @Forbidden
    public Map<String, Object> fillKerberosConfig(String allConfString, Long clusterId);

    /**
     * 更新缓存
     */
    @Forbidden
    public void updateCache(Long engineId, Integer componentCode);

    @Forbidden
    public List<Component> listComponent(Long engineId);

    @Forbidden
    public String getClusterLocalKerberosDir(Long clusterId);

    @Forbidden
    public void addComponentWithConfig(Long engineId, String confName, JSONObject config);

    public KerberosConfig getKerberosConfig(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType);

    @Forbidden
    public Map<String, String> getSFTPConfig(Long clusterId);

    public ComponentVO addOrUpdateComponent(@Param("clusterId") Long clusterId, @Param("componentConfig") String componentConfig,
                                            @Param("resources") List<Resource> resources, @Param("hadoopVersion") String hadoopVersion,
                                            @Param("kerberosFileName") String kerberosFileName, @Param("componentTemplate") String componentTemplate,
                                            @Param("componentCode") Integer componentCode);

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    public void closeKerberos(@Param("componentId") Long componentId);


    public Map<String, Object> addOrCheckClusterWithName(@Param("clusterName") String clusterName);

    /**
     * parse zip中xml或者json
     *
     * @param resources
     * @return
     */
    public List<Object> config(@Param("resources") List<Resource> resources, @Param("componentType") Integer componentType,@Param("autoDelete") Boolean autoDelete);

    @Forbidden
    public String buildSftpPath(Long clusterId, Integer componentCode);


    /**
     * 获取本地kerberos配置地址
     *
     * @param clusterId
     * @param componentCode
     * @return
     */
    @Forbidden
    public String getLocalKerberosPath(Long clusterId, Integer componentCode);

    /**
     * 下载文件
     *
     * @param componentId
     * @param downloadType 0:kerberos配置文件 1:配置文件 2:模板文件
     * @return
     */
    public File downloadFile(@Param("componentId") Long componentId, @Param("type") Integer downloadType, @Param("componentType") Integer componentType,
                             @Param("hadoopVersion") String hadoopVersion, @Param("clusterName") String clusterName);

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @param componentType
     * @return
     */
    public List<ClientTemplate> loadTemplate(@Param("componentType") Integer componentType, @Param("clusterName") String clusterName, @Param("version") String version);

    /**
     * 根据组件类型转换对应的插件名称
     * 如果只配yarn 需要调用插件时候 hdfs给默认值
     *
     * @param clusterName
     * @param componentType
     * @param version
     * @return
     */
    @Forbidden
    public String convertComponentTypeToClient(String clusterName, Integer componentType, String version);

    /**
     * 删除组件
     *
     * @param componentIds
     */
    public void delete(@Param("componentIds") List<Long> componentIds);

    /***
     * 获取对应的组件版本信息
     * @return
     */
    public Map getComponentVersion();

    @Forbidden
    public Component getComponentByClusterId(Long clusterId, Integer componentType);

    /**
     * 测试所有组件连通性
     * @param clusterName
     * @return
     */
    public List<ComponentTestResult> testConnects(@Param("clusterName") String clusterName);

}
