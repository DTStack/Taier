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
    public String listConfigOfComponents( Long dtUicTenantId,  Integer engineType);

    public Component getOne( Long id);

    public KerberosConfig getKerberosConfig( Long clusterId,  Integer componentType);

    public ComponentVO addOrUpdateComponent( Long clusterId,  String componentConfig,
                                             List<Resource> resources,  String hadoopVersion,
                                             String kerberosFileName,  String componentTemplate,
                                             Integer componentCode);

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    public void closeKerberos( Long componentId);


    public Map<String, Object> addOrCheckClusterWithName( String clusterName);

    /**
     * parse zip中xml或者json
     *
     * @param resources
     * @return
     */
    public List<Object> config( List<Resource> resources,  Integer componentType, Boolean autoDelete);

    /**
     * 下载文件
     *
     * @param componentId
     * @param downloadType 0:kerberos配置文件 1:配置文件 2:模板文件
     * @return
     */
    public File downloadFile( Long componentId,  Integer downloadType,  Integer componentType,
                              String hadoopVersion,  String clusterName);

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @param componentType
     * @return
     */
    public List<ClientTemplate> loadTemplate( Integer componentType,  String clusterName,  String version);


    /**
     * 删除组件
     *
     * @param componentIds
     */
    public void delete( List<Long> componentIds);

    /***
     * 获取对应的组件版本信息
     * @return
     */
    public Map getComponentVersion();

    /**
     * 测试所有组件连通性
     * @param clusterName
     * @return
     */
    public List<ComponentTestResult> testConnects( String clusterName);

}
