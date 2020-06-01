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

    @Forbidden
    public void addDefaultCluster() throws Exception;

    public ClusterVO addCluster(ClusterDTO clusterDTO);

    @Forbidden
    public ClusterVO getClusterByName(String clusterName);

    public PageResult<List<ClusterVO>> pageQuery(@Param("currentPage") int currentPage, @Param("pageSize") int pageSize);

    /**
     * 对外接口
     */
    public String clusterInfo(@Param("tenantId") Long tenantId);

    public String clusterExtInfo(@Param("tenantId") Long uicTenantId);

    /**
     * 对外接口
     */
    public JSONObject pluginInfoJSON(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr, @Param("dtUicUserId")Long dtUicUserId, @Param("deployMode")Integer deployMode);

    public String pluginInfo(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr,@Param("userId") Long dtUicUserId,@Param("deployMode")Integer deployMode);

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     * @param tenantId
     * @return
     */
    public String clusterSftpDir(@Param("tenantId") Long tenantId, @Param("componentType") Integer componentType);

    /**
     * 对外接口
     * FIXME 这里获取的hiveConf其实是spark thrift server的连接信息，后面会统一做修改
     */
    public String hiveInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String hiveServerInfo(@Param("tenantId") Long dtUicTenantId,@Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String hadoopInfo(@Param("tenantId") Long dtUicTenantId,@Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String carbonInfo(@Param("tenantId") Long dtUicTenantId,@Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String impalaInfo(@Param("tenantId") Long dtUicTenantId,@Param("fullKerberos") Boolean fullKerberos);

    /**
     * 对外接口
     */
    public String sftpInfo(@Param("tenantId") Long dtUicTenantId);

    @Forbidden
    public JSONObject buildClusterConfig(ClusterVO cluster);

    @Forbidden
    public ClusterVO getClusterByTenant(Long dtUicTenantId);

    public String getConfigByKey(@Param("dtUicTenantId")Long dtUicTenantId, @Param("key") String key,@Param("fullKerberos") Boolean fullKerberos);

    @Forbidden
    public Map<String, Object> getConfig(ClusterVO cluster, Long dtUicTenantId, String key);

    /**
     * 如果开启集群开启了kerberos认证，kerberosConfig中还需要包含hdfs配置
     *
     * @param key
     * @param cluster
     * @param kerberosConfig
     * @param configObj
     */
    @Forbidden
    public void addKerberosConfigWithHdfs(String key, ClusterVO cluster, KerberosConfig kerberosConfig, JSONObject configObj);

    public JSONObject convertSQLComponent(JSONObject jdbcInfo, JSONObject pluginInfo);

    /**
     * 集群下拉列表
     */
    public List<ClusterVO> clusters();

    @Forbidden
    public Cluster getOne(Long clusterId);

    public String tiDBInfo(@Param("tenantId") Long dtUicTenantId, @Param("userId") Long dtUicUserId);

    public String oracleInfo(@Param("tenantId") Long dtUicTenantId,@Param("userId") Long dtUicUserId);

    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    public void deleteCluster(@Param("clusterId")Long clusterId);

    /**
     * 获取集群信息详情 需要根据组件分组
     * @param clusterId
     * @return
     */
    public ClusterVO getCluster(@Param("clusterId") Long clusterId, @Param("kerberosConfig") Boolean kerberosConfig,@Param("removeTypeName") Boolean removeTypeName);


    public List<ClusterEngineVO> getAllCluster();


}
