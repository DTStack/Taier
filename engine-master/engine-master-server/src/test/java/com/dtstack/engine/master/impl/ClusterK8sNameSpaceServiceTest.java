package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.enums.EComponentScheduleType;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2020-06-04
 */
@PrepareForTest({AkkaConfig.class, ClientOperator.class})
public class ClusterK8sNameSpaceServiceTest extends AbstractTest {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private ComponentDao componentDao;

    @Mock
    private ClientOperator clientOperator;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private QueueDao queueDao;

    @Spy
    private TenantService tenantService;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Mock
    private ConsoleCache consoleCache;

    @Autowired
    private QueueService queueService;

    private String testClusterName = "testK8s";

    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(AkkaConfig.class);
        when(AkkaConfig.isLocalMode()).thenReturn(true);
        PowerMockito.mockStatic(ClientOperator.class);

        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);
        when(ClientOperator.getInstance()).thenReturn(clientOperator);

        when(clientOperator.testConnect(any(),any())).thenReturn(componentTestResult);

        ReflectionTestUtils.setField(tenantService,"clusterDao", clusterDao);
        ReflectionTestUtils.setField(tenantService,"queueDao", queueDao);
        ReflectionTestUtils.setField(tenantService,"tenantDao", tenantDao);
        ReflectionTestUtils.setField(tenantService,"engineTenantDao", engineTenantDao);
        ReflectionTestUtils.setField(tenantService,"engineDao", engineDao);
        ReflectionTestUtils.setField(tenantService,"consoleCache", consoleCache);
        doNothing().when(tenantService).checkClusterCanUse(any());

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
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
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
        Component sftpConfig = componentDao.getByClusterIdAndComponentType(clusterVO.getId(), EComponentType.SFTP.getTypeCode());
        Map sftpMap = JSONObject.parseObject(sftpConfig.getComponentConfig(), Map.class);
        //测试组件联通性
        ComponentTestResult componentTestResult = componentService.testConnect(k8s.getComponentTypeCode(), k8s.getComponentConfig(), testClusterName, k8s.getHadoopVersion(), engineId, null, sftpMap);
        Assert.assertNotNull(componentTestResult);
        Assert.assertTrue(componentTestResult.getResult());


        queueService.addNamespaces(engineId,"testK8s");
        List<Queue> queues = queueDao.listByEngineId(engineId);
        //添加测试租户
        Tenant tenant = DataCollection.getData().getTenant();
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
        componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"172.16.100.168\",\"username\":\"root\"}",
                null, "hadoop2", "", "[]", EComponentType.SFTP.getTypeCode());
        return componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{\"kubernetes.context\":\"# Configuration snippets may be placed in this directory as well\\nincludedir /etc/krb5.conf.d/\\n\\n[logging]\\n default = FILE:/var/log/krb5libs.log\\n kdc = FILE:/var/log/krb5kdc.log\\n admin_server = FILE:/var/log/kadmind.log\\n\\n[libdefaults]\\n dns_lookup_realm = false\\n ticket_lifetime = 24h\\n renew_lifetime = 7d\\n forwardable = true\\n rdns = false\\n pkinit_anchors = FILE:/etc/pki/tls/certs/ca-bundle.crt\\n default_realm = DTSTACK.COM\\n #default_ccache_name = KEYRING:persistent:%{uid}\\n\\n[realms]\\n DTSTACK.COM = {\\n  kdc = 172.16.10.99\\n  admin_server = 172.16.10.99\\n }\\n\\n[domain_realm]\\n .example.com = DTSTACK.COM\\n example.com = DTSTACK.COM\\n\"}"
                , null, "hadoop2", "", "[]", EComponentType.KUBERNETES.getTypeCode());
    }


    private ComponentVO testAddHdfs(ClusterVO clusterVO) {
        return componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{\"fs.defaultFS\":\"hdfs://ns1\",\"dfs.replication\":\"1\",\"dfs.ha.fencing.methods\":\"sshfence\",\"dfs.client.failover.proxy.provider.ns1\":\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"dfs.ha.fencing.ssh.private-key-files\":\"~/.ssh/id_rsa\",\"dfs.nameservices\":\"ns1\",\"fs.hdfs.impl.disable.cache\":\"true\",\"dfs.safemode.threshold.pct\":\"0.5\",\"dfs.ha.namenodes.ns1\":\"nn1,nn2\",\"dfs.namenode.name.dir\":\"file:/data/hadoop/hdfs/name\",\"dfs.journalnode.rpc-address\":\"0.0.0.0:8485\",\"fs.trash.interval\":\"14400\",\"dfs.journalnode.http-address\":\"0.0.0.0:8480\",\"dfs.namenode.rpc-address.ns1.nn2\":\"172.16.101.136:9000\",\"dfs.namenode.rpc-address.ns1.nn1\":\"172.16.100.216:9000\"," +
                        "\"hive.metastore.warehouse.dir\":\"/dtInsight/hive/warehouse\",\"hive.server2.async.exec.threads\":\"200\",\"dfs.datanode.data.dir\":\"file:/data/hadoop/hdfs/data\"," +
                        "\"dfs.namenode.shared.edits.dir\":\"qjournal://172.16.100.216:8485;172.16.101.136:8485;172.16.101.227:8485/namenode-ha-data\",\"hive.metastore.schema.verification\":\"false\",\"hive.server2.support.dynamic.service.discovery\":\"true\",\"hive.server2.session.check.interval\":\"30000\",\"hive.metastore.uris\":\"thrift://172.16.101.227:9083\",\"hive.server2.thrift.port\":\"10000\",\"hive.exec.dynamic.partition.mode\":\"nonstrict\",\"ha.zookeeper.session-timeout.ms\":\"5000\",\"hadoop.tmp.dir\":\"/data/hadoop_${user.name}\",\"dfs.journalnode.edits.dir\":\"/data/hadoop/hdfs/journal\",\"hive.server2.zookeeper.namespace\":\"hiveserver2\",\"hive.server2.enable.doAs\":\"false\",\"dfs.namenode.http-address.ns1.nn2\":\"172.16.101.136:50070\",\"dfs.namenode.http-address.ns1.nn1\":\"172.16.100.216:50070\"," +
                        "\"dfs.namenode.datanode.registration.ip-hostname-check\":\"false\",\"hadoop.proxyuser.${user.name}.hosts\":\"*\",\"hadoop.proxyuser.${user.name}.groups\":\"*\",\"hive.exec.scratchdir\":\"/dtInsight/hive/warehouse\",\"hive.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"datanucleus.schema.autoCreateAll\":\"true\",\"hive.exec.dynamic.partition\":\"true\",\"hive.cluster.delegation.token.store.class\":\"org.apache.hadoop.hive.thrift.MemoryTokenStore\",\"ha.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"hive.server2.thrift.min.worker.threads\":\"300\",\"dfs.ha.automatic-failover.enabled\":\"true\"}"
                , null, "hadoop2", "", "[]", EComponentType.HDFS.getTypeCode());
    }

}
