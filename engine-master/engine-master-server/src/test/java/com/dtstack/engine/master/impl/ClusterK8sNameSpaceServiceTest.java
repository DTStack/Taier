package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.utils.Template;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2020-06-04
 */
public class ClusterK8sNameSpaceServiceTest extends AbstractTest {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private EngineDao engineDao;

    @MockBean
    private ClientOperator clientOperator;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private QueueService queueService;

    private String testClusterName = "testK8s";

    @Before
    public void setup() throws Exception{

        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);

        when(clientOperator.testConnect(any(),any())).thenReturn(componentTestResult);
    }

    public void testCreateCluster() {
        componentService.addOrCheckClusterWithName(testClusterName);
    }

    public ClusterVO testGetClusterByName() {
        ClusterVO dbCluster = clusterService.getClusterByName(testClusterName);
        Assert.assertNotNull(dbCluster);
        return dbCluster;
    }

    /**
     * @see ComponentService#addOrCheckClusterWithName(String)
     * @see ComponentService#addOrUpdateComponent(Long, String, List, String, String, String, Integer)
     * @see ComponentService#getOne(Long)
     * @see ClusterService#getAllCluster()
     * @see ClusterService#getCluster(Long, Boolean, Boolean)
     * @see ClusterService#pageQuery(int, int)
     * @see ComponentService#delete(List)
     * @see ComponentService#testConnects(String)
     * @see ClusterService#deleteCluster(Long)
     * @see TenantService#bindingTenant(Long, Long, Long, String,String)
     * @see TenantService#bindingQueue(Long, Long)
     * @see TenantService#pageQuery(Long, Integer, String, int, int)
     * @see ComponentService#listConfigOfComponents(Long, Integer)
     * @see ComponentService#getKerberosConfig(Long, Integer)
     * @see ComponentService#convertComponentTypeToClient(String, Integer, String)
     * @see EngineService#getQueue(Long)
     * @see EngineService#listSupportEngine(Long)
     * @see EngineService#listClusterEngines(Long, boolean)
     */
    @Test
    @Transactional
    @Rollback
    public void testGetCluster() throws Exception{
        //创建集群
        testCreateCluster();
        ClusterVO clusterVO = testGetClusterByName();
        Assert.assertNotNull(clusterVO.getClusterId());
        //添加组件 添加引擎
        ComponentVO k8sComponent = testAddK8s(clusterVO);
        Assert.assertNotNull(k8sComponent.getId());
        Component k8s = componentService.getOne(k8sComponent.getId());
        testAddHdfs(clusterVO);
        Assert.assertNotNull(k8s);
        //点击详情接口
        testGetCluster(clusterVO);
        List<Engine> engines = engineDao.listByClusterId(clusterVO.getId());
        Assert.assertNotNull(engines);
        Long engineId = engines.stream().map(Engine::getId).collect(Collectors.toList()).get(0);
        Map sftpMap = componentService.getComponentByClusterId(clusterVO.getClusterId(),EComponentType.SFTP.getTypeCode(),false,Map.class);
        String k8sConfig = componentService.getComponentByClusterId(clusterVO.getClusterId(),EComponentType.KUBERNETES.getTypeCode(),false,String.class);
        //测试组件联通性
        ComponentTestResult componentTestResult = componentService.testConnect(k8s.getComponentTypeCode(), k8sConfig, testClusterName, k8s.getHadoopVersion(), engineId, null, sftpMap,null);
        Assert.assertNotNull(componentTestResult);
        Assert.assertTrue(componentTestResult.getResult());


        queueService.addNamespaces(engineId,"testK8s");
        List<Queue> queues = queueDao.listByEngineId(engineId);
        //添加测试租户
        Tenant tenant = Template.getTenantTemplate();
        tenant.setDtUicTenantId(-108L);
        tenantDao.insert(tenant);
        tenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertNotNull(tenant);
        Assert.assertNotNull(tenant.getId());
        //绑定租户
        tenantService.bindingTenant(tenant.getDtUicTenantId(),clusterVO.getClusterId(),queues.get(0).getId(),"","testbb");
        //查询集群信息
        PageResult<List<EngineTenantVO>> engineTenants = tenantService.pageQuery(clusterVO.getClusterId(), MultiEngineType.HADOOP.getType(), tenant.getTenantName(), 10, 1);
        Assert.assertNotNull(engineTenants);
        Assert.assertNotNull(engineTenants.getData());
        testGetAllCluster(clusterVO);
        testPageQuery(clusterVO);

    }


    private void testPageQuery(ClusterVO clusterVO) {
        PageResult<List<EngineTenantVO>> listPageResult = tenantService.pageQuery(clusterVO.getClusterId(), MultiEngineType.HADOOP.getType(), "", 20, 1);
        Assert.assertNotNull(listPageResult);
        List<EngineTenantVO> data = listPageResult.getData();
        Assert.assertNotNull(data);
    }

    private void testGetCluster(ClusterVO clusterVO) {
        //测试yarn 和hdfs是否存在
        //单个
        ClusterVO cluster = clusterService.getCluster(clusterVO.getClusterId(), null, true);
        Assert.assertNotNull(cluster);
        Assert.assertNotNull(cluster.getScheduling());
        Optional<SchedulingVo> commonSchedule = cluster.getScheduling().stream().filter(s -> s.getSchedulingCode() == EComponentScheduleType.COMMON.getType()).findFirst();
        Assert.assertTrue(commonSchedule.isPresent());
        List<ComponentVO> components = commonSchedule.get().getComponents();
        Assert.assertNotNull(components);
        Optional<ComponentVO> sftpComponent = components.stream().filter(c -> c.getComponentTypeCode() == EComponentType.SFTP.getTypeCode()).findAny();
        Assert.assertTrue(sftpComponent.isPresent());
        Optional<SchedulingVo> resourceSchedule = cluster.getScheduling().stream().filter(s -> s.getSchedulingCode() == EComponentScheduleType.RESOURCE.getType()).findFirst();
        Assert.assertTrue(resourceSchedule.isPresent());
        Optional<ComponentVO> k8sComponent = resourceSchedule.get().getComponents().stream().filter(c -> c.getComponentTypeCode() == EComponentType.KUBERNETES.getTypeCode()).findAny();
        Assert.assertTrue(k8sComponent.isPresent());

    }

    private void testGetAllCluster(ClusterVO clusterVO) {
        List<ClusterEngineVO> allCluster = clusterService.getAllCluster();
        Assert.assertNotNull(allCluster);
        for (ClusterEngineVO clusterEngineVO : allCluster) {
            if (clusterEngineVO.getClusterId().equals(clusterVO.getClusterId())) {
                Assert.assertNotNull(clusterEngineVO.getEngines());
                List<EngineVO> engines = clusterEngineVO.getEngines();
                Assert.assertNotNull(engines);
            }
        }
    }

    private ComponentVO testAddK8s(ClusterVO clusterVO) {
        String sftpTemplate = "[{\"key\":\"auth\",\"required\":true,\"type\":\"RADIO_LINKAGE\",\"value\":\"1\",\"values\":[{\"dependencyKey\":\"auth\",\"dependencyValue\":\"1\",\"key\":\"password\",\"required\":true,\"type\":\"PASSWORD\",\"value\":\"1\",\"values\":[{\"dependencyKey\":\"auth$password\",\"dependencyValue\":\"\",\"key\":\"password\",\"required\":true,\"type\":\"PASSWORD\",\"value\":\"\"}]},{\"dependencyKey\":\"auth\",\"dependencyValue\":\"2\",\"key\":\"rsaPath\",\"required\":true,\"type\":\"\",\"value\":\"2\",\"values\":[{\"dependencyKey\":\"auth$rsaPath\",\"dependencyValue\":\"\",\"key\":\"rsaPath\",\"required\":true,\"type\":\"INPUT\",\"value\":\"\"}]}]},{\"key\":\"fileTimeout\",\"required\":true,\"type\":\"INPUT\",\"value\":\"300000\"},{\"key\":\"host\",\"required\":true,\"type\":\"INPUT\",\"value\":\"127.0.0.1\"},{\"key\":\"isUsePool\",\"required\":true,\"type\":\"INPUT\",\"value\":\"true\"},{\"key\":\"maxIdle\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"},{\"key\":\"maxTotal\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"},{\"key\":\"maxWaitMillis\",\"required\":true,\"type\":\"INPUT\",\"value\":\"3600000\"},{\"key\":\"minIdle\",\"required\":true,\"type\":\"INPUT\",\"value\":\"16\"},{\"key\":\"path\",\"required\":true,\"type\":\"INPUT\",\"value\":\"/data/sftp\"},{\"key\":\"port\",\"required\":true,\"type\":\"INPUT\",\"value\":\"22\"},{\"key\":\"timeout\",\"required\":true,\"type\":\"INPUT\",\"value\":\"0\"},{\"key\":\"username\",\"required\":true,\"type\":\"INPUT\",\"value\":\"admin\"}]";
        componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{}",
                null, "hadoop2", "", sftpTemplate, EComponentType.SFTP.getTypeCode(),null,null,null,false);
        return componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{}"
                , null, "hadoop2", "", "[{\"key\":\"kubernetes.context\",\"required\":true,\"type\":\"INPUT\",\"value\":\"121212\"}]", EComponentType.KUBERNETES.getTypeCode(),null,null,null,false);
    }


    private ComponentVO testAddHdfs(ClusterVO clusterVO) {
        return componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{\"fs.defaultFS\":\"hdfs://ns1\",\"dfs.replication\":\"1\",\"dfs.ha.fencing.methods\":\"sshfence\",\"dfs.client.failover.proxy.provider.ns1\":\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"dfs.ha.fencing.ssh.private-key-files\":\"~/.ssh/id_rsa\",\"dfs.nameservices\":\"ns1\",\"fs.hdfs.impl.disable.cache\":\"true\",\"dfs.safemode.threshold.pct\":\"0.5\",\"dfs.ha.namenodes.ns1\":\"nn1,nn2\",\"dfs.namenode.name.dir\":\"file:/data/hadoop/hdfs/name\",\"dfs.journalnode.rpc-address\":\"0.0.0.0:8485\",\"fs.trash.interval\":\"14400\",\"dfs.journalnode.http-address\":\"0.0.0.0:8480\",\"dfs.namenode.rpc-address.ns1.nn2\":\"172.16.101.136:9000\",\"dfs.namenode.rpc-address.ns1.nn1\":\"172.16.100.216:9000\"," +
                        "\"hive.metastore.warehouse.dir\":\"/dtInsight/hive/warehouse\",\"hive.server2.async.exec.threads\":\"200\",\"dfs.datanode.data.dir\":\"file:/data/hadoop/hdfs/data\"," +
                        "\"dfs.namenode.shared.edits.dir\":\"qjournal://172.16.100.216:8485;172.16.101.136:8485;172.16.101.227:8485/namenode-ha-data\",\"hive.metastore.schema.verification\":\"false\",\"hive.server2.support.dynamic.service.discovery\":\"true\",\"hive.server2.session.check.interval\":\"30000\",\"hive.metastore.uris\":\"thrift://172.16.101.227:9083\",\"hive.server2.thrift.port\":\"10000\",\"hive.exec.dynamic.partition.mode\":\"nonstrict\",\"ha.zookeeper.session-timeout.ms\":\"5000\",\"hadoop.tmp.dir\":\"/data/hadoop_${user.name}\",\"dfs.journalnode.edits.dir\":\"/data/hadoop/hdfs/journal\",\"hive.server2.zookeeper.namespace\":\"hiveserver2\",\"hive.server2.enable.doAs\":\"false\",\"dfs.namenode.http-address.ns1.nn2\":\"172.16.101.136:50070\",\"dfs.namenode.http-address.ns1.nn1\":\"172.16.100.216:50070\"," +
                        "\"dfs.namenode.datanode.registration.ip-hostname-check\":\"false\",\"hadoop.proxyuser.${user.name}.hosts\":\"*\",\"hadoop.proxyuser.${user.name}.groups\":\"*\",\"hive.exec.scratchdir\":\"/dtInsight/hive/warehouse\",\"hive.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"datanucleus.schema.autoCreateAll\":\"true\",\"hive.exec.dynamic.partition\":\"true\",\"hive.cluster.delegation.token.store.class\":\"org.apache.hadoop.hive.thrift.MemoryTokenStore\",\"ha.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"hive.server2.thrift.min.worker.threads\":\"300\",\"dfs.ha.automatic-failover.enabled\":\"true\"}"
                , null, "hadoop2", "", "[]", EComponentType.HDFS.getTypeCode(),null,null,null,false);
    }

}
