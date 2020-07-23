package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ComponentService extends DtInsightServer {
    /**
     * {
     * "1":{
     * "xx":"xx"
     * }
     * }
     */
    @RequestLine("POST /node/component/listConfigOfComponents")
    String listConfigOfComponents( Long dtUicTenantId,  Integer engineType);

    @RequestLine("POST /node/component/getOne")
    Component getOne( Long id);

    @RequestLine("POST /node/component/getKerberosConfig")
    KerberosConfig getKerberosConfig( Long clusterId,  Integer componentType);

    @RequestLine("POST /node/component/addOrUpdateComponent")
    ComponentVO addOrUpdateComponent( Long clusterId,  String componentConfig,
                                      List<Resource> resources,  String hadoopVersion,
                                      String kerberosFileName,  String componentTemplate,
                                      Integer componentCode);

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    @RequestLine("POST /node/component/closeKerberos")
    void closeKerberos( Long componentId);


    @RequestLine("POST /node/component/addOrCheckClusterWithName")
    Map<String, Object> addOrCheckClusterWithName( String clusterName);

    /**
     * parse zip中xml或者json
     *
     * @param resources
     * @return
     */
    @RequestLine("POST /node/component/config")
    List<Object> config( List<Resource> resources,  Integer componentType, Boolean autoDelete);

    /**
     * 下载文件
     *
     * @param componentId
     * @param downloadType 0:kerberos配置文件 1:配置文件 2:模板文件
     * @return
     */
    @RequestLine("POST /node/component/downloadFile")
    File downloadFile( Long componentId,  Integer downloadType,  Integer componentType,
                       String hadoopVersion,  String clusterName);

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @param componentType
     * @return
     */
    @RequestLine("POST /node/component/loadTemplate")
    List<ClientTemplate> loadTemplate( Integer componentType,  String clusterName,  String version);


    /**
     * 删除组件
     *
     * @param componentIds
     */
    @RequestLine("POST /node/component/delete")
    void delete( List<Long> componentIds);

    /***
     * 获取对应的组件版本信息
     * @return
     */
    @RequestLine("POST /node/component/getComponentVersion")
    Map getComponentVersion();

    /**
     * 测试所有组件连通性
     * @param clusterName
     * @return
     */
    @RequestLine("POST /node/component/testConnects")
    List<ComponentTestResult> testConnects( String clusterName);

}
