package com.dtstack.engine.api.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;



import java.util.List;

public interface ClusterService extends DtInsightServer {

    @RequestLine("POST /node/cluster/addCluster")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<ClusterVO> addCluster(ClusterDTO clusterDTO);

    @RequestLine("POST /node/cluster/pageQuery")
    ApiResponse<PageResult<List<ClusterVO>>> pageQuery(@Param("currentPage") int currentPage, @Param("pageSize") int pageSize);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/clusterInfo")
    ApiResponse<String> clusterInfo(@Param("tenantId") Long tenantId);

    @RequestLine("POST /node/cluster/clusterExtInfo")
    ApiResponse<String> clusterExtInfo(@Param("tenantId") Long uicTenantId);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/pluginInfoJSON")
    ApiResponse<JSONObject> pluginInfoJSON(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr, @Param("dtUicUserId") Long dtUicUserId, @Param("deployMode") Integer deployMode);

    @RequestLine("POST /node/cluster/pluginInfo")
    ApiResponse<String> pluginInfo(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr, @Param("dtUicUserId") Long dtUicUserId, @Param("deployMode") Integer deployMode);

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     * @param tenantId
     * @return
     */
    @RequestLine("POST /node/cluster/clusterSftpDir")
    ApiResponse<String> clusterSftpDir(@Param("tenantId") Long tenantId, @Param("componentType") Integer componentType);

    /**
     * 对外接口
     * FIXME 这里获取的hiveConf其实是spark thrift server的连接信息，后面会统一做修改
     */
    @RequestLine("POST /node/cluster/hiveInfo")
    ApiResponse<String> hiveInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/hiveServerInfo")
    ApiResponse<String> hiveServerInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/hadoopInfo")
    ApiResponse<String> hadoopInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/carbonInfo")
    ApiResponse<String> carbonInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/impalaInfo")
    ApiResponse<String> impalaInfo(@Param(value = "tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/sftpInfo")
    ApiResponse<String> sftpInfo(@Param("tenantId") Long dtUicTenantId);


    @RequestLine("POST /node/cluster/getConfigByKey")
    ApiResponse<String> getConfigByKey(@Param("dtUicTenantId") Long dtUicTenantId, @Param("key") String key, @Param("fullKerberos") Boolean fullKerberos);


    /**
     * 集群下拉列表
     */
    @RequestLine("POST /node/cluster/clusters")
    ApiResponse<List<ClusterVO>> clusters();

    @RequestLine("POST /node/cluster/tiDBInfo")
    ApiResponse<String> tiDBInfo(@Param("tenantId") Long dtUicTenantId, @Param("userId") Long dtUicUserId);

    @RequestLine("POST /node/cluster/oracleInfo")
    ApiResponse<String> oracleInfo(@Param("tenantId") Long dtUicTenantId, @Param("userId") Long dtUicUserId);

    @RequestLine("POST /node/cluster/greenplumInfo")
    ApiResponse<String> greenplumInfo(@Param("tenantId") Long dtUicTenantId, @Param("userId") Long dtUicUserId);

    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    @RequestLine("POST /node/cluster/deleteCluster")
    ApiResponse deleteCluster( @Param("clusterId") Long clusterId);

    /**
     * 获取集群信息详情 需要根据组件分组
     * @param clusterId
     * @return
     */
    @RequestLine("POST /node/cluster/getCluster")
    ApiResponse<ClusterVO> getCluster(@Param("clusterId") Long clusterId, @Param("kerberosConfig") Boolean kerberosConfig, @Param("removeTypeName") Boolean removeTypeName);

    @RequestLine("POST /node/cluster/getAllCluster")
    ApiResponse<List<ClusterEngineVO>> getAllCluster();

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/prestoInfo")
    ApiResponse<String> prestoInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);


}
