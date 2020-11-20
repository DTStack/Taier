package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.enums.DbType;
import com.dtstack.engine.api.enums.EComponentApiType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.sdk.core.feign.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/node/cluster", "/node/component/cluster"})
@Api(value = "/node/cluster", tags = {"集群接口"})
public class ClusterController{

    @Autowired
    private ClusterService clusterService;

    @RequestMapping(value="/addCluster", method = {RequestMethod.POST})
    public ClusterVO addCluster(@RequestBody ClusterDTO clusterDTO) {
        return clusterService.addCluster(clusterDTO);
    }

    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    public PageResult<List<ClusterVO>> pageQuery(@DtRequestParam("currentPage") int currentPage, @DtRequestParam("pageSize") int pageSize) {
        return clusterService.pageQuery(currentPage, pageSize);
    }

    @RequestMapping(value="/clusterInfo", method = {RequestMethod.POST})
    public String clusterInfo(@DtRequestParam("tenantId") Long tenantId) {
        return clusterService.clusterInfo(tenantId);
    }

    @RequestMapping(value="/clusterExtInfo", method = {RequestMethod.POST})
    public ClusterVO clusterExtInfo(@DtRequestParam("tenantId") Long uicTenantId) {
        return clusterService.clusterExtInfo(uicTenantId);
    }

    @RequestMapping(value="/pluginInfoJSON", method = {RequestMethod.POST})
    public JSONObject pluginInfoJSON(@DtRequestParam("tenantId") Long dtUicTenantId, @DtRequestParam("engineType") String engineTypeStr, @DtRequestParam("dtUicUserId")Long dtUicUserId, @DtRequestParam("deployMode")Integer deployMode) {
        return clusterService.pluginInfoJSON(dtUicTenantId, engineTypeStr, dtUicUserId, deployMode);
    }


    @RequestMapping(value="/pluginInfo", method = {RequestMethod.POST})
    public String pluginInfo(@DtRequestParam("tenantId") Long dtUicTenantId, @DtRequestParam("engineType") String engineTypeStr,@DtRequestParam("userId") Long dtUicUserId,@DtRequestParam("deployMode")Integer deployMode) {
        return clusterService.pluginInfo(dtUicTenantId, engineTypeStr, dtUicUserId, deployMode);
    }

    @RequestMapping(value="/clusterSftpDir", method = {RequestMethod.POST})
    @ApiOperation(value = "获取集群在sftp上的路径")
    public String clusterSftpDir(@DtRequestParam("tenantId") Long tenantId, @DtRequestParam("componentType") Integer componentType) {
        return clusterService.clusterSftpDir(tenantId, componentType);
    }

    @ApiOperation(value = "获得插件信息")
    @RequestMapping(value="/pluginInfoForType", method = {RequestMethod.POST})
    public String pluginInfoForType(@DtRequestParam("tenantId") Long dtUicTenantId  , @DtRequestParam("fullKerberos") Boolean fullKerberos, @DtRequestParam("pluginType") EComponentApiType pluginType){
        return clusterService.pluginInfoForType(dtUicTenantId, fullKerberos,pluginType.getTypeCode());
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/hiveInfo", method = {RequestMethod.POST})
    public String hiveInfo(@DtRequestParam("tenantId") Long dtUicTenantId, @DtRequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hiveInfo(dtUicTenantId, fullKerberos);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/hiveServerInfo", method = {RequestMethod.POST})
    public String hiveServerInfo(@DtRequestParam("tenantId") Long dtUicTenantId,@DtRequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hiveServerInfo(dtUicTenantId, fullKerberos);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/hadoopInfo", method = {RequestMethod.POST})
    public String hadoopInfo(@DtRequestParam("tenantId") Long dtUicTenantId,@DtRequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hadoopInfo(dtUicTenantId, fullKerberos);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/carbonInfo", method = {RequestMethod.POST})
    public String carbonInfo(@DtRequestParam("tenantId") Long dtUicTenantId,@DtRequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.carbonInfo(dtUicTenantId, fullKerberos);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/impalaInfo", method = {RequestMethod.POST})
    public String impalaInfo(@DtRequestParam("tenantId") Long dtUicTenantId,@DtRequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.impalaInfo(dtUicTenantId, fullKerberos);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @return
     */
    @RequestMapping(value="/sftpInfo", method = {RequestMethod.POST})
    public String sftpInfo(@DtRequestParam("tenantId") Long dtUicTenantId) {
        return clusterService.sftpInfo(dtUicTenantId);
    }

    @RequestMapping(value="/getConfigByKey", method = {RequestMethod.POST})
    public String getConfigByKey(@DtRequestParam("dtUicTenantId")Long dtUicTenantId, @DtRequestParam("key") String key, @DtRequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.getConfigByKey(dtUicTenantId, key, fullKerberos);
    }

    @RequestMapping(value="/clusters", method = {RequestMethod.POST})
    @ApiOperation(value = "集群下拉列表")
    public List<ClusterVO> clusters() {
        return clusterService.clusters();
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @return
     */
    @RequestMapping(value="/tiDBInfo", method = {RequestMethod.POST})
    public String tiDBInfo(@DtRequestParam("tenantId") Long dtUicTenantId, @DtRequestParam("userId") Long dtUicUserId) {
        return clusterService.tiDBInfo(dtUicTenantId, dtUicUserId);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @return
     */
    @RequestMapping(value="/oracleInfo", method = {RequestMethod.POST})
    public String oracleInfo(@DtRequestParam("tenantId") Long dtUicTenantId,@DtRequestParam("userId") Long dtUicUserId) {
        return clusterService.oracleInfo(dtUicTenantId, dtUicUserId);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @return
     */
    @RequestMapping(value="/greenplumInfo", method = {RequestMethod.POST})
    public String greenplumInfo(@DtRequestParam("tenantId") Long dtUicTenantId,@DtRequestParam("userId") Long dtUicUserId) {
        return clusterService.greenplumInfo(dtUicTenantId, dtUicUserId);
    }

    @RequestMapping(value="/dbInfo", method = {RequestMethod.POST})
    public String dbInfo(@DtRequestParam("tenantId") Long dtUicTenantId, @DtRequestParam("userId") Long dtUicUserId, @DtRequestParam("type") DbType type) {
        return clusterService.dbInfo(dtUicTenantId, dtUicUserId , type.getTypeCode());
    }

    @RequestMapping(value="/deleteCluster", method = {RequestMethod.POST})
    @ApiOperation(value = "删除集群")
    public void deleteCluster(@DtRequestParam("clusterId")Long clusterId) {
        clusterService.deleteCluster(clusterId);
    }

    @RequestMapping(value="/getCluster", method = {RequestMethod.POST})
    @ApiOperation(value = "获取集群信息详情")
    public ClusterVO getCluster(@DtRequestParam("clusterId") Long clusterId, @DtRequestParam("kerberosConfig") Boolean kerberosConfig,@DtRequestParam("removeTypeName") Boolean removeTypeName) {
        return clusterService.getCluster(clusterId, kerberosConfig, removeTypeName,true);
    }

    @RequestMapping(value="/getAllCluster", method = {RequestMethod.POST})
    public List<ClusterEngineVO> getAllCluster() {
        return clusterService.getAllCluster();
    }

    @RequestMapping(value="/prestoInfo", method = {RequestMethod.POST})
    public String prestoInfo(@DtRequestParam("tenantId") Long dtUicTenantId, @DtRequestParam("fullKerberos") Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.PRESTO_SQL.getConfName(), fullKerberos);
    }
}
