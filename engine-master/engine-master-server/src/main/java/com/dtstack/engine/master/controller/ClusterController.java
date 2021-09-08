package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.dto.ClusterDTO;
import com.dtstack.engine.common.enums.DbType;
import com.dtstack.engine.common.enums.EComponentApiType;
import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.master.vo.ClusterEngineVO;
import com.dtstack.engine.master.vo.ClusterVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
    public PageResult<List<ClusterVO>> pageQuery(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize) {
        return clusterService.pageQuery(currentPage, pageSize);
    }

    @RequestMapping(value="/clusterInfo", method = {RequestMethod.POST})
    public String clusterInfo(@RequestParam("tenantId") Long tenantId) {
        return clusterService.clusterInfo(tenantId);
    }

    @RequestMapping(value="/clusterExtInfo", method = {RequestMethod.POST})
    public ClusterVO clusterExtInfo(@RequestParam("tenantId") Long uicTenantId) {
        return clusterService.clusterExtInfo(uicTenantId,false);
    }

    @RequestMapping(value="/pluginInfoJSON", method = {RequestMethod.POST})
    public JSONObject pluginInfoJSON(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") String engineTypeStr, @RequestParam("dtUicUserId")Long dtUicUserId, @RequestParam("deployMode")Integer deployMode) {
        return clusterService.pluginInfoJSON(dtUicTenantId, engineTypeStr, dtUicUserId, deployMode,null);
    }


    @RequestMapping(value="/pluginInfo", method = {RequestMethod.POST})
    public String pluginInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") String engineTypeStr, @RequestParam("userId") Long dtUicUserId, @RequestParam("deployMode")Integer deployMode) {
        return clusterService.pluginInfo(dtUicTenantId, engineTypeStr, dtUicUserId, deployMode);
    }

    @RequestMapping(value="/clusterSftpDir", method = {RequestMethod.POST})
    @ApiOperation(value = "获取集群在sftp上的路径")
    public String clusterSftpDir(@RequestParam("tenantId") Long tenantId, @RequestParam("componentType") Integer componentType) {
        return clusterService.clusterSftpDir(tenantId, componentType);
    }

    @ApiOperation(value = "获得插件信息")
    @RequestMapping(value="/pluginInfoForType", method = {RequestMethod.POST})
    public String pluginInfoForType(@RequestParam("tenantId") Long dtUicTenantId  , @RequestParam("fullKerberos") Boolean fullKerberos, @RequestParam("pluginType") EComponentApiType pluginType){
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
    public String hiveInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hiveInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/hiveServerInfo", method = {RequestMethod.POST})
    public String hiveServerInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hiveServerInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/hadoopInfo", method = {RequestMethod.POST})
    public String hadoopInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hadoopInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/carbonInfo", method = {RequestMethod.POST})
    public String carbonInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.carbonInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/impalaInfo", method = {RequestMethod.POST})
    public String impalaInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.impalaInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @return
     */
    @RequestMapping(value="/sftpInfo", method = {RequestMethod.POST})
    public String sftpInfo(@RequestParam("tenantId") Long dtUicTenantId) {
        return clusterService.sftpInfo(dtUicTenantId);
    }

    @RequestMapping(value="/getConfigByKey", method = {RequestMethod.POST})
    public String getConfigByKey(@RequestParam("dtUicTenantId")Long dtUicTenantId, @RequestParam("key") String key, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.getConfigByKey(dtUicTenantId, key, fullKerberos,null);
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
    public String tiDBInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("userId") Long dtUicUserId) {
        return clusterService.tiDBInfo(dtUicTenantId, dtUicUserId,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @return
     */
    @RequestMapping(value="/oracleInfo", method = {RequestMethod.POST})
    public String oracleInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("userId") Long dtUicUserId) {
        return clusterService.oracleInfo(dtUicTenantId, dtUicUserId,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @return
     */
    @RequestMapping(value="/greenplumInfo", method = {RequestMethod.POST})
    public String greenplumInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("userId") Long dtUicUserId) {
        return clusterService.greenplumInfo(dtUicTenantId, dtUicUserId,null);
    }

    @RequestMapping(value="/dbInfo", method = {RequestMethod.POST})
    public String dbInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("userId") Long dtUicUserId, @RequestParam("type") DbType type) {
        return clusterService.dbInfo(dtUicTenantId, dtUicUserId , type.getTypeCode());
    }

    @RequestMapping(value="/deleteCluster", method = {RequestMethod.POST})
    @ApiOperation(value = "删除集群")
    public void deleteCluster(@RequestParam("clusterId")Long clusterId) {
        clusterService.deleteCluster(clusterId);
    }

    @RequestMapping(value="/getCluster", method = {RequestMethod.POST})
    @ApiOperation(value = "获取集群信息详情")
    public ClusterVO getCluster(@RequestParam("clusterId") Long clusterId, @RequestParam("kerberosConfig") Boolean kerberosConfig, @RequestParam("removeTypeName") Boolean removeTypeName) {
        return clusterService.getCluster(clusterId, removeTypeName, true,true);
    }

    @RequestMapping(value="/getAllCluster", method = {RequestMethod.POST})
    public List<ClusterEngineVO> getAllCluster() {
        return clusterService.getAllCluster();
    }

    @RequestMapping(value="/prestoInfo", method = {RequestMethod.POST})
    public String prestoInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.PRESTO_SQL.getConfName(), fullKerberos);
    }


    @ApiOperation(value = "判断的租户和另一个租户是否在一个集群")
    @ApiImplicitParams({
            @ApiImplicitParam(name="tenantId",value="租户id",required=true, dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name="aimTenantIds",value="租户id集合",required=true, dataType = "Long", allowMultiple = true)
    })
    @RequestMapping(value="/isSameCluster", method = {RequestMethod.POST})
    public Boolean isSameCluster(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("aimTenantIds") List<Long> dtUicTenantIds){
        return clusterService.isSameCluster(dtUicTenantId,dtUicTenantIds);
    }


    @ApiOperation(value = "判断的租户对应集群是否有standalone组件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "componentType", value = "组件类型", required = true, dataType = "Integer", allowMultiple = true)
    })
    @RequestMapping(value = "/hasStandalone", method = {RequestMethod.POST, RequestMethod.GET})
    public Boolean hasStandalone(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("componentType") Integer componentType) {
        return clusterService.hasStandalone(dtUicTenantId, componentType);
    }
}
