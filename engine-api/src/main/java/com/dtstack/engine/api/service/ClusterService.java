package com.dtstack.engine.api.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterVO;

import java.util.List;
import java.util.Map;

public interface ClusterService {

    public ClusterVO addCluster(ClusterDTO clusterDTO);


    public PageResult<List<ClusterVO>> pageQuery( int currentPage,  int pageSize);

    /**
     * 对外接口
     */
    public String clusterInfo( Long tenantId);

    public String clusterExtInfo( Long uicTenantId);

    /**
     * 对外接口
     */
    public JSONObject pluginInfoJSON( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId, Integer deployMode);

    public String pluginInfo( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId,Integer deployMode);

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     * @param tenantId
     * @return
     */
    public String clusterSftpDir( Long tenantId,  Integer componentType);

    /**
     * 对外接口
     * FIXME 这里获取的hiveConf其实是spark thrift server的连接信息，后面会统一做修改
     */
    public String hiveInfo( Long dtUicTenantId,  Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String hiveServerInfo( Long dtUicTenantId, Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String hadoopInfo( Long dtUicTenantId, Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String carbonInfo( Long dtUicTenantId, Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String impalaInfo( Long dtUicTenantId, Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String sftpInfo( Long dtUicTenantId);


    public String getConfigByKey(Long dtUicTenantId,  String key, Boolean fullKerberos);


    /**
     * 集群下拉列表
     */
    public List<ClusterVO> clusters();

    public String tiDBInfo( Long dtUicTenantId,  Long dtUicUserId);

    public String oracleInfo( Long dtUicTenantId, Long dtUicUserId);

    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    public void deleteCluster(Long clusterId);

    /**
     * 获取集群信息详情 需要根据组件分组
     * @param clusterId
     * @return
     */
    public ClusterVO getCluster( Long clusterId,  Boolean kerberosConfig, Boolean removeTypeName);


    public List<ClusterEngineVO> getAllCluster();


}
