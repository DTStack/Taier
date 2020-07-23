package com.dtstack.engine.api.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface ClusterService extends DtInsightServer {

    @RequestLine("POST /node/cluster/addCluster")
    ClusterVO addCluster(ClusterDTO clusterDTO);

    @RequestLine("POST /node/cluster/pageQuery")
    PageResult<List<ClusterVO>> pageQuery( int currentPage,  int pageSize);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/clusterInfo")
    String clusterInfo( Long tenantId);

    @RequestLine("POST /node/cluster/clusterExtInfo")
    String clusterExtInfo( Long uicTenantId);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/pluginInfoJSON")
    JSONObject pluginInfoJSON( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId, Integer deployMode);

    @RequestLine("POST /node/cluster/pluginInfo")
    String pluginInfo( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId,Integer deployMode);

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     * @param tenantId
     * @return
     */
    @RequestLine("POST /node/cluster/clusterSftpDir")
    String clusterSftpDir( Long tenantId,  Integer componentType);

    /**
     * 对外接口
     * FIXME 这里获取的hiveConf其实是spark thrift server的连接信息，后面会统一做修改
     */
    @RequestLine("POST /node/cluster/hiveInfo")
    String hiveInfo( Long dtUicTenantId,  Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/hiveServerInfo")
    String hiveServerInfo( Long dtUicTenantId, Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/hadoopInfo")
    String hadoopInfo( Long dtUicTenantId, Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/carbonInfo")
    String carbonInfo( Long dtUicTenantId, Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/impalaInfo")
    String impalaInfo( Long dtUicTenantId, Boolean fullKerberos);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/sftpInfo")
    String sftpInfo( Long dtUicTenantId);


    @RequestLine("POST /node/cluster/getConfigByKey")
    String getConfigByKey( Long dtUicTenantId,  String key, Boolean fullKerberos);


    /**
     * 集群下拉列表
     */
    @RequestLine("POST /node/cluster/clusters")
    List<ClusterVO> clusters();

    @RequestLine("POST /node/cluster/tiDBInfo")
    String tiDBInfo( Long dtUicTenantId,  Long dtUicUserId);

    @RequestLine("POST /node/cluster/oracleInfo")
    String oracleInfo( Long dtUicTenantId, Long dtUicUserId);

    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    @RequestLine("POST /node/cluster/deleteCluster")
    void deleteCluster( Long clusterId);

    /**
     * 获取集群信息详情 需要根据组件分组
     * @param clusterId
     * @return
     */
    @RequestLine("POST /node/cluster/getCluster")
    ClusterVO getCluster( Long clusterId,  Boolean kerberosConfig, Boolean removeTypeName);

    @RequestLine("POST /node/cluster/getAllCluster")
    List<ClusterEngineVO> getAllCluster();


}
