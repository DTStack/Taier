package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.master.impl.ClusterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/node/cluster")
@Api(value = "/node/cluster", tags = {"集群接口"})
public class ClusterController {

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
    public String clusterExtInfo(@RequestParam("tenantId") Long uicTenantId) {
        return clusterService.clusterExtInfo(uicTenantId);
    }

    @RequestMapping(value="/pluginInfoJSON", method = {RequestMethod.POST})
    public JSONObject pluginInfoJSON(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") String engineTypeStr, @RequestParam("dtUicUserId")Long dtUicUserId, @RequestParam("deployMode")Integer deployMode) {
        return clusterService.pluginInfoJSON(dtUicTenantId, engineTypeStr, dtUicUserId, deployMode);
    }

    @RequestMapping(value="/tenantId", method = {RequestMethod.POST})
    public String pluginInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") String engineTypeStr,@RequestParam("userId") Long dtUicUserId,@RequestParam("deployMode")Integer deployMode) {
        return clusterService.pluginInfo(dtUicTenantId, engineTypeStr, dtUicUserId, deployMode);
    }

    @RequestMapping(value="/clusterSftpDir", method = {RequestMethod.POST})
    @ApiOperation(value = "获取集群在sftp上的路径")
    public String clusterSftpDir(@RequestParam("tenantId") Long tenantId, @RequestParam("componentType") Integer componentType) {
        return clusterService.clusterSftpDir(tenantId, componentType);
    }

    @RequestMapping(value="/hiveInfo", method = {RequestMethod.POST})
    public String hiveInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hiveInfo(dtUicTenantId, fullKerberos);
    }

    @RequestMapping(value="/hiveServerInfo", method = {RequestMethod.POST})
    public String hiveServerInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hiveServerInfo(dtUicTenantId, fullKerberos);
    }

    @RequestMapping(value="/hadoopInfo", method = {RequestMethod.POST})
    public String hadoopInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hadoopInfo(dtUicTenantId, fullKerberos);
    }

    @RequestMapping(value="/carbonInfo", method = {RequestMethod.POST})
    public String carbonInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.carbonInfo(dtUicTenantId, fullKerberos);
    }

    @RequestMapping(value="/impalaInfo", method = {RequestMethod.POST})
    public String impalaInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.impalaInfo(dtUicTenantId, fullKerberos);
    }

    @RequestMapping(value="/sftpInfo", method = {RequestMethod.POST})
    public String sftpInfo(@RequestParam("tenantId") Long dtUicTenantId) {
        return clusterService.sftpInfo(dtUicTenantId);
    }

    @RequestMapping(value="/getConfigByKey", method = {RequestMethod.POST})
    public String getConfigByKey(@RequestParam("dtUicTenantId")Long dtUicTenantId, @RequestParam("key") String key, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.getConfigByKey(dtUicTenantId, key, fullKerberos);
    }

    @RequestMapping(value="/clusters", method = {RequestMethod.POST})
    @ApiOperation(value = "集群下拉列表")
    public List<ClusterVO> clusters() {
        return clusterService.clusters();
    }

    @RequestMapping(value="/tiDBInfo", method = {RequestMethod.POST})
    public String tiDBInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("userId") Long dtUicUserId) {
        return clusterService.tiDBInfo(dtUicTenantId, dtUicUserId);
    }

    @RequestMapping(value="/oracleInfo", method = {RequestMethod.POST})
    public String oracleInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("userId") Long dtUicUserId) {
        return clusterService.oracleInfo(dtUicTenantId, dtUicUserId);
    }

    @RequestMapping(value="/deleteCluster", method = {RequestMethod.POST})
    @ApiOperation(value = "删除集群")
    public void deleteCluster(@RequestParam("clusterId")Long clusterId) {
        clusterService.deleteCluster(clusterId);
    }

    @RequestMapping(value="/getCluster", method = {RequestMethod.POST})
    @ApiOperation(value = "获取集群信息详情")
    public ClusterVO getCluster(@RequestParam("clusterId") Long clusterId, @RequestParam("kerberosConfig") Boolean kerberosConfig,@RequestParam("removeTypeName") Boolean removeTypeName) {
        return clusterService.getCluster(clusterId, kerberosConfig, removeTypeName);
    }

    @RequestMapping(value="/getAllCluster", method = {RequestMethod.POST})
    public List<ClusterEngineVO> getAllCluster() {
        return clusterService.getAllCluster();
    }
}
