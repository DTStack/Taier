package com.dtstack.engine.api.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.enums.DbType;
import com.dtstack.engine.api.enums.EComponentApiType;
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

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/clusterExtInfo")
    ApiResponse<ClusterVO> clusterExtInfo(@Param("tenantId") Long uicTenantId);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/pluginInfoJSON")
    ApiResponse<JSONObject> pluginInfoJSON(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr, @Param("dtUicUserId") Long dtUicUserId, @Param("deployMode") Integer deployMode);

    @RequestLine("POST /node/cluster/pluginInfo")
    ApiResponse<String> pluginInfo(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr, @Param("userId") Long dtUicUserId, @Param("deployMode") Integer deployMode);

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     * @param tenantId
     * @return
     */
    @RequestLine("POST /node/cluster/clusterSftpDir")
    ApiResponse<String> clusterSftpDir(@Param("tenantId") Long tenantId, @Param("componentType") Integer componentType);

    /**
     * 获得插件信息
     * 注释： 用于取代 /node/cluster/hiveInfo /node/cluster/hiveServerInfo、/node/cluster/hadoopInfo、/node/cluster/carbonInfo、/node/cluster/impalaInfo、/node/cluster/sftpInfo等接口
     * @param dtUicTenantId 用户id
     * @param fullKerberos 是否将sftp中keytab配置转换为本地路径
     *                     如果不传或者false,不转换;
     *                     如果是true,转换;
     * @param pluginType 插件类型 插入code即可
     *                   HDFS(4, "HDFS", "hadoopConf"), -> /node/cluster/hadoopInfo
     *                   SPARK_THRIFT(6, "SparkThrift", "hiveConf"), -> /node/cluster/hiveInfo
     *                   CARBON_DATA(7, "CarbonData ThriftServer", "carbonConf"), -> /node/cluster/carbonInfo
     *                   HIVE_SERVER(9, "HiveServer", "hiveServerConf"), ->  /node/cluster/hiveServerInfo
     *                   IMPALA_SQL(11, "Impala SQL", "impalaSqlConf"), -> /node/cluster/impalaInfo
     *                   SFTP(10, "SFTP", "sftpConf"), -> /node/cluster/sftpInfo
     * @return
     */
    @RequestLine("POST /node/cluster/pluginInfoForType")
    ApiResponse<String> pluginInfoForType(@Param("tenantId") Long dtUicTenantId  ,@Param("fullKerberos") Boolean fullKerberos, @Param("pluginType") EComponentApiType pluginType );

    /**
     * 通过枚举获得配置
     *
     * @param dtUicTenantId 用户id
     * @param key 枚举类型 例如 FLINK
     * @param fullKerberos 是否将sftp中keytab配置转换为本地路径
     *                     如果不传或者false,不转换;
     *                     如果是true,转换;
     * @return
     */
    @RequestLine("POST /node/cluster/getConfigByKey")
    ApiResponse<String> getConfigByKey(@Param("dtUicTenantId") Long dtUicTenantId, @Param("key") String key, @Param("fullKerberos") Boolean fullKerberos);


    /**
     * 集群下拉列表
     */
    @RequestLine("POST /node/cluster/clusters")
    ApiResponse<List<ClusterVO>> clusters();

    /**
     * 获得tiDBInfo,oracleInfo,greenplumInfo组件信息
     * 注释： 用于取代 /node/cluster/tiDBInfo、/node/cluster/oracleInfo 、/node/cluster/greenplumInfo 等接口
     * @param dtUicTenantId 组户id
     * @param dtUicUserId 用户id
     * @param type 组件类型
     *             `Oracle(2);` /node/cluster/oracleInfo
     *             `TiDB(31);` /node/cluster/tiDBInfo
     *             `GREENPLUM6(36);` /node/cluster/greenplumInfo
     * @return
     */
    @RequestLine("POST /node/cluster/dbInfo")
    ApiResponse<String> dbInfo(@Param("tenantId") Long dtUicTenantId, @Param("userId") Long dtUicUserId, @Param("type") DbType type);

    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    @RequestLine("POST /node/cluster/deleteCluster")
    ApiResponse<Void> deleteCluster( @Param("clusterId") Long clusterId);


    /**
     * 获取集群信息详情 需要根据组件分组
     * @param clusterId
     * @return
     */
    @RequestLine("POST /node/cluster/getCluster")
    ApiResponse<ClusterVO> getCluster(@Param("clusterId") Long clusterId, @Param("kerberosConfig") Boolean kerberosConfig, @Param("removeTypeName") Boolean removeTypeName);

    /**
     * 获得所有Cluster
     *
     * @return
     */
    @RequestLine("POST /node/cluster/getAllCluster")
    ApiResponse<List<ClusterEngineVO>> getAllCluster();

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/prestoInfo")
    ApiResponse<String> prestoInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);


}
