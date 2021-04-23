package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface ComponentService extends DtInsightServer {

    @RequestLine("POST /node/component/listConfigOfComponents")
    ApiResponse<List<ComponentsConfigOfComponentsVO>> listConfigOfComponents(@Param("tenantId") Long dtUicTenantId, @Param("engineType") Integer engineType);

    @RequestLine("POST /node/component/getOne")
    ApiResponse<Component> getOne(@Param("id") Long id);

    @RequestLine("POST /node/component/getKerberosConfig")
    ApiResponse<KerberosConfig> getKerberosConfig(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType);

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    @RequestLine("POST /node/component/closeKerberos")
    ApiResponse<Void> closeKerberos(@Param("componentId") Long componentId);

    @RequestLine("POST /node/component/addOrCheckClusterWithName")
    ApiResponse<ComponentsResultVO> addOrCheckClusterWithName(@Param("clusterName") String clusterName);

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @param componentType
     * @return
     */
    @RequestLine("POST /node/component/loadTemplate")
    ApiResponse<List<ClientTemplate>> loadTemplate(@Param("componentType") Integer componentType, @Param("clusterName") String clusterName, @Param("version") String version);

    /**
     * 删除组件
     *
     * @param componentIds
     */
    @RequestLine("POST /node/component/delete")
    ApiResponse<Void> delete( @Param("componentIds") List<Long> componentIds);

    /***
     * 获取对应的组件版本信息
     * @return
     */
    @RequestLine("POST /node/component/getComponentVersion")
    ApiResponse<Map> getComponentVersion();

    /**
     * 测试所有组件连通性
     * @param clusterName
     * @return
     */
    @RequestLine("POST /node/component/testConnects")
    ApiResponse<List<ComponentTestResult>> testConnects(@Param("clusterName") String clusterName);

    @RequestLine("POST /node/component/isYarnSupportGpus")
    ApiResponse<Boolean> isYarnSupportGpus(@Param("clusterName") String clusterName);


}
