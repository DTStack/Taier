package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.jobdealer.resource.ComputeResourceType;
import com.dtstack.engine.master.jobdealer.resource.FlinkResource;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.lineage.dao.LineageDataSetDao;
import com.dtstack.lineage.impl.LineageDataSetInfoService;
import com.dtstack.lineage.impl.LineageDataSourceService;
import com.dtstack.schedule.common.enums.AppType;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2020-06-04
 */
public class ClusterServiceTest extends AbstractTest {


    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private LineageDataSetDao lineageDataSetDao;

    @Autowired
    private ComponentDao componentDao;

    @Spy
    private LineageDataSetInfoService dataSetInfoService;

    @Autowired
    private LineageDataSourceService dataSourceService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private TenantResourceDao tenantResourceDao;

    @Autowired
    private EngineDao engineDao;


    @MockBean
    private ClientOperator clientOperator;

    @Autowired
    private QueueDao queueDao;

    @Spy
    private TenantService tenantService;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @MockBean
    private ConsoleCache consoleCache;

    @Autowired
    private EngineService engineService;

    private String testClusterName = "testcase";

    @Before
    public void setup() throws Exception{

        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);

        ComponentTestResult.ClusterResourceDescription clusterResourceDescription = new ComponentTestResult.ClusterResourceDescription(1024,1024,1024,new ArrayList<>());
        componentTestResult.setClusterResourceDescription(clusterResourceDescription);
        when(clientOperator.testConnect(any(),any())).thenReturn(componentTestResult);


        ReflectionTestUtils.setField(tenantService,"clusterDao", clusterDao);
        ReflectionTestUtils.setField(tenantService,"queueDao", queueDao);
        ReflectionTestUtils.setField(tenantService,"tenantDao", tenantDao);
        ReflectionTestUtils.setField(tenantService,"engineTenantDao", engineTenantDao);
        ReflectionTestUtils.setField(tenantService,"engineDao", engineDao);
        ReflectionTestUtils.setField(tenantService,"consoleCache", consoleCache);
        ReflectionTestUtils.setField(tenantService,"tenantResourceDao", tenantResourceDao);
        doNothing().when(tenantService).checkClusterCanUse(any());

        ReflectionTestUtils.setField(dataSetInfoService,"sourceService", dataSourceService);
        ReflectionTestUtils.setField(dataSetInfoService,"tenantDao", tenantDao);
        ReflectionTestUtils.setField(dataSetInfoService,"lineageDataSetDao", lineageDataSetDao);
        ReflectionTestUtils.setField(dataSetInfoService,"componentDao", componentDao);
        ReflectionTestUtils.setField(dataSetInfoService,"tenantDao", tenantDao);

        when(dataSetInfoService.getClient(any(),any(),any())).thenReturn(null);
        when(dataSetInfoService.getAllColumns(any(),any())).thenReturn(new ArrayList<>());




    }

    public void testCreateCluster(String clusterName) {
        componentService.addOrCheckClusterWithName(clusterName);
    }

    public ClusterVO testGetClusterByName() {
        ClusterVO dbCluster = clusterService.getClusterByName(testClusterName);
        Assert.assertNotNull(dbCluster);
        return dbCluster;
    }


    @Test
    public void testCreateEmpty(){
        try {
            testCreateCluster("");
            testCreateCluster("123456789101234567891012345");
        } catch (Exception e) {
        }
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
     * @see TenantService#bindingTenant(Long, Long, Long, String)
     * @see TenantService#bindingQueue(Long, Long, String)
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
        testCreateCluster(testClusterName);
        ClusterVO clusterVO = testGetClusterByName();
        Assert.assertNotNull(clusterVO.getClusterId());
        //添加组件 添加引擎
        ComponentVO yarnComponent = testAddYarn(clusterVO);
        Assert.assertNotNull(yarnComponent.getId());
        Component yarn = componentService.getOne(yarnComponent.getId());
        ComponentVO hdfsComponent = testAddHdfs(clusterVO);
        testAddHiveWithKerberos(clusterVO);
        testAddSpark(clusterVO);
        Assert.assertNotNull(yarn);

        //添加sftp组件


        //校验查询接口
        testGetAllCluster(clusterVO, yarnComponent);
        //页面展示接口
        testPageQuery();
        //点击详情接口
        clusterVO = testGetCluster(clusterVO);
        List<Engine> engines = engineDao.listByClusterId(clusterVO.getId());
        Assert.assertNotNull(engines);
        Long engineId = engines.stream().map(Engine::getId).collect(Collectors.toList()).get(0);
        Component sftpConfig = componentDao.getByClusterIdAndComponentType(clusterVO.getId(), EComponentType.SFTP.getTypeCode());
        Map sftpMap = JSONObject.parseObject(sftpConfig.getComponentConfig(), Map.class);
        //测试组件联通性
        ComponentTestResult componentTestResult = componentService.testConnect(yarn.getComponentTypeCode(), yarn.getComponentConfig(), testClusterName, yarn.getHadoopVersion(), engineId, null, sftpMap,yarnComponent.getStoreType());
        Assert.assertNotNull(componentTestResult);
        Assert.assertTrue(componentTestResult.getResult());

        //添加测试组件对应yarn的队列
        Queue queue = this.testInsertQueue(engineId);
        //添加测试租户
        Tenant tenant = Template.getTenantTemplate();
        tenant.setDtUicTenantId(-107L);
        tenantDao.insert(tenant);
        tenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertNotNull(tenant);
        Assert.assertNotNull(tenant.getId());
        //绑定租户
        this.testBindTenant(clusterVO, queue);
        this.testIsSame(clusterVO,queue,tenant);
        //切换队列
        this.testUpdateQueue(engineId, tenant);

        this.checkQueryWithUicTenantId(tenant.getDtUicTenantId());

        //查询集群信息
        PageResult<List<EngineTenantVO>> engineTenants = tenantService.pageQuery(clusterVO.getClusterId(), MultiEngineType.HADOOP.getType(), tenant.getTenantName(), 10, 1);
        Assert.assertNotNull(engineTenants);
        Assert.assertNotNull(engineTenants.getData());
        //查询集群组件信息
        JSONArray componentsJson = JSONObject.parseArray(JSON.toJSONString(componentService.listConfigOfComponents(tenant.getDtUicTenantId(), MultiEngineType.HADOOP.getType())));
        Assert.assertNotNull(componentsJson);
        this.testFlinkResource(tenant);

        //查询kerberos配置信息
        KerberosConfig kerberosConfig = componentService.getKerberosConfig(clusterVO.getId(), EComponentType.YARN.getTypeCode());
        Assert.assertNull(kerberosConfig);
        this.checkQueryWithUicTenantId(tenant);

        //loadTemplate
        String typeName = componentService.convertComponentTypeToClient(testClusterName, EComponentType.SPARK.getTypeCode(),"210",null);
        Assert.assertEquals(typeName,"yarn2-hdfs2-spark210");

        //查询队列信息
        List<QueueVO> queueVOS = engineService.getQueue(engineId);
        Assert.assertNotNull(queueVOS);

        //查询配置信息
        JSONArray engineJson = JSONObject.parseArray(JSON.toJSONString(engineService.listSupportEngine(tenant.getDtUicTenantId())));
        Assert.assertTrue(engineJson.size() > 0);
        
        List<EngineVO> engineVOS = engineService.listClusterEngines(clusterVO.getId(), true);
        Assert.assertNotNull(engineVOS);

        //新增或修改逻辑数据源
        Long sourceId = addOrUpdateDataSource(tenant.getDtUicTenantId());
        Assert.assertNotNull(sourceId);

        //根据appType查询逻辑数据源
        LineageDataSource dataSource = getDataSourceByIdAndAppType(sourceId);
        Assert.assertNotNull(dataSource);

        //获取表信息
        LineageDataSetInfo dataSet = getOneBySourceIdAndDbNameAndTableName(dataSource.getId());
        Assert.assertNotNull(dataSet);

        //获取表字段列表信息
        List<Column> columnList = getTableColumns(dataSet.getSourceId(), dataSet.getTableName(), dataSet.getSchemaName(),
                dataSet.getDbName());
        Assert.assertNotNull(columnList);

        //删除组件
        try {
            //删除组件
            componentService.delete(Lists.newArrayList(hdfsComponent.getId().intValue()));
        } catch (Exception e) {
            Assert.assertTrue( e instanceof RdosDefineException);
        }

        //删除集群
        try {
            clusterService.deleteCluster(clusterVO.getClusterId());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("有租户"));
        }
    }

    private void testFlinkResource(Tenant tenant) {
        try {
            JobClient jobClient = new JobClient();
            jobClient.setTenantId(tenant.getDtUicTenantId());
            jobClient.setComputeType(ComputeType.BATCH);
            Properties properties = PublicUtil.stringToProperties("## 任务运行方式：\n" +
                    "## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步\n" +
                    "## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session\n" +
                    "## flinkTaskRunMode=per_job\n" +
                    "## per_job模式下jobManager配置的内存大小，默认1024（单位M)\n" +
                    "## jobmanager.memory.mb=1024\n" +
                    "## per_job模式下taskManager配置的内存大小，默认1024（单位M）\n" +
                    "## taskmanager.memory.mb=1024\n" +
                    "## per_job模式下每个taskManager 对应 slot的数量\n" +
                    "## slots=1\n" +
                    "## checkpoint保存时间间隔\n" +
                    "## flink.checkpoint.interval=300000\n" +
                    "## 任务优先级, 范围:1-1000\n" +
                    "## job.priority=10");
            ReflectionTestUtils.setField(jobClient,"confProperties",properties);
            FlinkResource commonResource = new FlinkResource();
            commonResource.setClusterDao(clusterDao);
            commonResource.setEngineDao(engineDao);
            commonResource.setClusterService(clusterService);
            commonResource.setComponentService(componentService);
            ComputeResourceType computeResourceType = commonResource.getComputeResourceType(jobClient);
            Assert.assertEquals(computeResourceType,ComputeResourceType.FlinkYarnSession);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Tenant testBindTenant(ClusterVO clusterVO, Queue queue) throws Exception {
        Tenant tenant = Template.getTenantTemplate();
        tenant.setDtUicTenantId(-107L);
        tenantDao.insert(tenant);
        tenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertNotNull(tenant);
        Assert.assertNotNull(tenant.getId());
        //绑定租户
        tenantService.bindingTenant(tenant.getDtUicTenantId(), clusterVO.getClusterId(), queue.getId(),"","");
        return tenant;
    }

    public Tenant testIsSame(ClusterVO clusterVO, Queue queue,Tenant tenant) throws Exception {
        Tenant sameTenant = Template.getTenantTemplate();
        sameTenant.setDtUicTenantId(-108L);
        tenantDao.insert(sameTenant);
        tenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertNotNull(tenant);
        Assert.assertNotNull(tenant.getId());
        //绑定租户
        tenantService.bindingTenant(sameTenant.getDtUicTenantId(), clusterVO.getClusterId(), queue.getId(),"","");
        clusterService.isSameCluster(-108L,Lists.newArrayList(tenant.getDtUicTenantId()));
        return tenant;
    }

    @Test
    public void testPluginJson(){

        JSONObject dumy = clusterService.pluginInfoJSON(null, "dummy", 1L, 0);
        Assert.assertEquals("dummy",dumy.getString("typeName"));
    }

    @Test
    public void testPluginInfo(){
        String s = clusterService.pluginInfo(null, null, null, null);
        Assert.assertEquals("{}",s);
    }



    private void checkQueryWithUicTenantId(Tenant tenant) {
        Long dtUicTenantId = tenant.getDtUicTenantId();
        String clusterInfo = clusterService.clusterInfo(dtUicTenantId);
        Assert.assertNotEquals(clusterInfo, StringUtils.EMPTY);
        ClusterVO clusterVO = clusterService.clusterExtInfo(dtUicTenantId);
        Assert.assertNotNull(clusterVO);
        JSONObject infoJSON = clusterService.pluginInfoJSON(dtUicTenantId, "hadoop", null, null);
        Assert.assertNotNull(infoJSON);
        String sftpDir = clusterService.clusterSftpDir(dtUicTenantId, EComponentType.HDFS.getTypeCode());
        Assert.assertEquals(sftpDir,"/data/sftp/" + AppType.CONSOLE + "_" + clusterVO.getClusterName() + File.separator + EComponentType.getByCode(EComponentType.HDFS.getTypeCode()).name());
       //查询集群信息
        PageResult<List<EngineTenantVO>> engineTenants = tenantService.pageQuery(clusterVO.getClusterId(), MultiEngineType.HADOOP.getType(), tenant.getTenantName(), 10, 1);
        Assert.assertNotNull(engineTenants);
        Assert.assertNotNull(engineTenants.getData());
        //查询集群组件信息
        JSONArray componentsJson = JSONObject.parseArray(JSON.toJSONString(componentService.listConfigOfComponents(tenant.getDtUicTenantId(), MultiEngineType.HADOOP.getType())));
        Assert.assertNotNull(componentsJson);

        //查询kerberos配置信息
        KerberosConfig kerberosConfig = componentService.getKerberosConfig(clusterVO.getId(), EComponentType.YARN.getTypeCode());
        Assert.assertNull(kerberosConfig);

        JSONObject sparkConf = clusterService.pluginInfoJSON(tenant.getDtUicTenantId(), EngineTypeComponentType.SPARK.name(), null, null);
        Assert.assertNotNull(sparkConf);
        List<ClusterEngineVO> allCluster = clusterService.getAllCluster();
        Assert.assertNotNull(allCluster);
        Assert.assertTrue(allCluster.stream().anyMatch(c -> c.getClusterName().equalsIgnoreCase(clusterVO.getClusterName())));
        Assert.assertNotNull(clusterService.getOne(clusterVO.getClusterId()));
        Assert.assertNotNull(clusterService.pluginInfoForType(tenant.getDtUicTenantId(),true,EComponentType.SPARK.getTypeCode()));
        Assert.assertNotNull(clusterService.pluginInfoForType(tenant.getDtUicTenantId(),true,EComponentType.HIVE_SERVER.getTypeCode()));
        Assert.assertNotNull(clusterService.hiveInfo(dtUicTenantId, true));
        List<ClusterVO> clusters = clusterService.clusters();
        Assert.assertNotNull(clusters);
        Assert.assertTrue(clusters.stream().anyMatch(c -> c.getClusterId().equals(clusterVO.getClusterId())));
    }

    private void checkQueryWithUicTenantId(Long dtUicTenantId) {
        String clusterInfo = clusterService.clusterInfo(dtUicTenantId);
        Assert.assertNotEquals(clusterInfo, StringUtils.EMPTY);
        ClusterVO clusterVO = clusterService.clusterExtInfo(dtUicTenantId);
        Assert.assertNotNull(clusterVO);
        JSONObject infoJSON = clusterService.pluginInfoJSON(dtUicTenantId, "hadoop", null, null);
        Assert.assertNotNull(infoJSON);
        String sftpDir = clusterService.clusterSftpDir(dtUicTenantId, EComponentType.HDFS.getTypeCode());
        Assert.assertEquals(sftpDir,"/data/sftp"+File.separator + AppType.CONSOLE + "_" + clusterVO.getClusterName() + File.separator + EComponentType.getByCode(EComponentType.HDFS.getTypeCode()).name());

    }

    private void testUpdateQueue(Long engineId, Tenant tenant) {
        Queue queueb = new Queue();
        queueb.setEngineId(engineId);
        queueb.setQueueName("default.b");
        queueb.setMaxCapacity("1.0");
        queueb.setCapacity("0.3");
        queueb.setQueueState("RUNNING");
        queueb.setParentQueueId(-1L);
        queueb.setQueuePath("default");
        queueDao.insert(queueb);
        tenantService.bindingQueue(queueb.getId(),tenant.getDtUicTenantId(),null);
    }

    private Queue testInsertQueue(Long engineId) {
        Queue queue = new Queue();
        queue.setEngineId(engineId);
        queue.setQueueName("default.a");
        queue.setMaxCapacity("1.0");
        queue.setCapacity("0.3");
        queue.setQueueState("RUNNING");
        queue.setParentQueueId(-1L);
        queue.setQueuePath("default");
        queueDao.insert(queue);
        return queue;
    }

    private void testPageQuery() {
        PageResult<List<ClusterVO>> listPageResult = clusterService.pageQuery(1, 100);
        Assert.assertNotNull(listPageResult);
        List<ClusterVO> data = listPageResult.getData();
        Assert.assertNotNull(data);
        Optional<ClusterVO> pageQueryVo = data.stream().filter(s -> s.getClusterName().equalsIgnoreCase(testClusterName)).findFirst();
        Assert.assertTrue(pageQueryVo.isPresent());
    }

    private ClusterVO testGetCluster(ClusterVO clusterVO) {
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
        Optional<ComponentVO> yarnComponent = resourceSchedule.get().getComponents().stream().filter(c -> c.getComponentTypeCode() == EComponentType.YARN.getTypeCode()).findAny();
        Assert.assertTrue(yarnComponent.isPresent());
        return cluster;
    }

    private void testGetAllCluster(ClusterVO clusterVO, ComponentVO yarnComponent) {
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

    private ComponentVO testAddYarn(ClusterVO clusterVO) {
        componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"172.16.100.168\",\"username\":\"root\"}",
                null, "hadoop2", "", "[]", EComponentType.SFTP.getTypeCode(),null,"","");
        return componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{\"yarn.resourcemanager.zk-address\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"yarn.resourcemanager.admin.address.rm1\":\"172.16.100.216:8033\",\"yarn.resourcemanager.webapp.address.rm2\":\"172.16.101.136:8088\",\"yarn.log.server.url\"" +
                        ":\"http://172.16.101.136:19888/jobhistory/logs/\",\"yarn.resourcemanager.admin.address.rm2\":\"172.16.101.136:8033\"," +
                        "\"yarn.resourcemanager.webapp.address.rm1\":\"172.16.100.216:8088\",\"yarn.resourcemanager.ha.rm-ids\":\"rm1,rm2\"," +
                        "\"yarn.resourcemanager.ha.automatic-failover.zk-base-path\":\"/yarn-leader-election\",\"yarn.client.failover-proxy-provider\":" +
                        "\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\"yarn.resourcemanager.scheduler.address.rm1\":\"172.16.100.216:8030\",\"" +
                        "yarn.resourcemanager.scheduler.address.rm2\":\"172.16.101.136:8030\",\"yarn.nodemanager.delete.debug-delay-sec\":\"600\",\"yarn.resourcemanager.address.rm1\":" +
                        "\"172.16.100.216:8032\",\"yarn.log-aggregation.retain-seconds\":\"2592000\",\"yarn.nodemanager.resource.memory-mb\":\"6144\",\"yarn.resourcemanager.ha.enabled\":\"true\"," +
                        "\"yarn.resourcemanager.address.rm2\":\"172.16.101.136:8032\",\"yarn.resourcemanager.cluster-id\":\"yarn-rm-cluster\"," +
                        "\"yarn.scheduler.minimum-allocation-mb\":\"512\",\"yarn.nodemanager.aux-services\":\"mapreduce_shuffle\"," +
                        "\"yarn.resourcemanager.resource-tracker.address.rm1\":\"172.16.100.216:8031\"," +
                        "\"yarn.nodemanager.resource.cpu-vcores\":\"8\",\"yarn.resourcemanager.resource-tracker.address.rm2\":\"" +
                        "172.16.101.136:8031\",\"yarn.nodemanager.pmem-check-enabled\":\"true\",\"yarn.nodemanager.remote-app-log-dir\":" +
                        "\"/tmp/logs\",\"yarn.scheduler.maximum-allocation-mb\":\"6144\",\"yarn.resourcemanager.ha.automatic-failover.enabled\":\"true\"," +
                        "\"yarn.nodemanager.vmem-check-enabled\":\"false\",\"yarn.log-aggregation.retain-check-interval-seconds\":\"604800\"," +
                        "\"yarn.nodemanager.webapp.address\":\"0.0.0.0:8042\",\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\":" +
                        "\"org.apache.hadoop.mapred.ShuffleHandler\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"yarn.log-aggregation-enable\":\"true\"," +
                        "\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"yarn.nodemanager.vmem-pmem-ratio\":\"4\"," +
                        "\"yarn.resourcemanager.zk-state-store.address\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"ha.zookeeper.quorum\":\"172.16.100.216:2181," +
                        "172.16.101.136:2181,172.16.101.227:2181\"}"
                , null, "hadoop2", "", "[]", EComponentType.YARN.getTypeCode(),null,"","");
    }



    private ComponentVO testAddHdfs(ClusterVO clusterVO) {
        return componentService.addOrUpdateComponent(clusterVO.getClusterId(), "{\"fs.defaultFS\":\"hdfs://ns1\",\"dfs.replication\":\"1\",\"dfs.ha.fencing.methods\":\"sshfence\",\"dfs.client.failover.proxy.provider.ns1\":\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"dfs.ha.fencing.ssh.private-key-files\":\"~/.ssh/id_rsa\",\"dfs.nameservices\":\"ns1\",\"fs.hdfs.impl.disable.cache\":\"true\",\"dfs.safemode.threshold.pct\":\"0.5\",\"dfs.ha.namenodes.ns1\":\"nn1,nn2\",\"dfs.namenode.name.dir\":\"file:/data/hadoop/hdfs/name\",\"dfs.journalnode.rpc-address\":\"0.0.0.0:8485\",\"fs.trash.interval\":\"14400\",\"dfs.journalnode.http-address\":\"0.0.0.0:8480\",\"dfs.namenode.rpc-address.ns1.nn2\":\"172.16.101.136:9000\",\"dfs.namenode.rpc-address.ns1.nn1\":\"172.16.100.216:9000\"," +
                        "\"hive.metastore.warehouse.dir\":\"/dtInsight/hive/warehouse\",\"hive.server2.async.exec.threads\":\"200\",\"dfs.datanode.data.dir\":\"file:/data/hadoop/hdfs/data\"," +
                        "\"dfs.namenode.shared.edits.dir\":\"qjournal://172.16.100.216:8485;172.16.101.136:8485;172.16.101.227:8485/namenode-ha-data\",\"hive.metastore.schema.verification\":\"false\",\"hive.server2.support.dynamic.service.discovery\":\"true\",\"hive.server2.session.check.interval\":\"30000\",\"hive.metastore.uris\":\"thrift://172.16.101.227:9083\",\"hive.server2.thrift.port\":\"10000\",\"hive.exec.dynamic.partition.mode\":\"nonstrict\",\"ha.zookeeper.session-timeout.ms\":\"5000\",\"hadoop.tmp.dir\":\"/data/hadoop_${user.name}\",\"dfs.journalnode.edits.dir\":\"/data/hadoop/hdfs/journal\",\"hive.server2.zookeeper.namespace\":\"hiveserver2\",\"hive.server2.enable.doAs\":\"false\",\"dfs.namenode.http-address.ns1.nn2\":\"172.16.101.136:50070\",\"dfs.namenode.http-address.ns1.nn1\":\"172.16.100.216:50070\"," +
                        "\"dfs.namenode.datanode.registration.ip-hostname-check\":\"false\",\"hadoop.proxyuser.${user.name}.hosts\":\"*\",\"hadoop.proxyuser.${user.name}.groups\":\"*\",\"hive.exec.scratchdir\":\"/dtInsight/hive/warehouse\",\"hive.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"datanucleus.schema.autoCreateAll\":\"true\",\"hive.exec.dynamic.partition\":\"true\",\"hive.cluster.delegation.token.store.class\":\"org.apache.hadoop.hive.thrift.MemoryTokenStore\",\"ha.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"hive.server2.thrift.min.worker.threads\":\"300\",\"dfs.ha.automatic-failover.enabled\":\"true\"}"
                , null, "hadoop2", "", "[]", EComponentType.HDFS.getTypeCode(),null,"","");
    }


    private void testAddHiveWithKerberos(ClusterVO clusterVO) {
        componentService.addOrUpdateComponent(clusterVO.getClusterId(),"{\"jdbcUrl\":\"jdbc:hive2://eng-cdh3:10001/default;principal=hive/eng-cdh3@DTSTACK.COM\",\"maxJobPoolSize\":\"\",\"minJobPoolSize\":\"\",\"password\":\"\",\"queue\":\"\",\"username\":\"\"}",
                null,"1.x","hive_pure.keytab.zip","[{\"key\":\"jdbcUrl\",\"values\":null,\"type\":\"INPUT\",\"value\":\"jdbc:hive2://eng-cdh3:10001/default;principal=hive/eng-cdh3@DTSTACK.COM\",\"required\":true,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"maxJobPoolSize\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"minJobPoolSize\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"password\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"queue\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null},{\"key\":\"username\",\"values\":null,\"type\":\"INPUT\",\"value\":\"\",\"required\":false,\"dependencyKey\":null,\"dependencyValue\":null}]",
                9,EComponentType.HDFS.getTypeCode(),"hive/eng-cdh3@DTSTACK.COM","hive/eng-cdh3@DTSTACK.COM");
    }

    private ComponentVO testAddSpark(ClusterVO clusterVO) {
        String componentConfig = "{\"deploymode\":[\"perjob\"],\"perjob\":{\"addColumnSupport\":\"true\",\"spark.eventLog.compress\":\"true\",\"spark.eventLog.dir\":\"hdfs://ns1/tmp/spark-yarn-logs\"," +
                "\"spark.eventLog.enabled\":\"true\",\"spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON\":\"/data/miniconda2/bin/python2\",\"spark.yarn.appMasterEnv.PYSPARK_PYTHON\":\"/data/anaconda3/bin/python3\"," +
                "\"sparkPythonExtLibPath\":\"/dtInsight/pythons/pyspark.zip,hdfs://ns1/dtInsight/pythons/py4j-0.10.7-src.zip\",\"sparkSqlProxyPath\":\"hdfs://ns1/dtInsight/spark/client/spark-sql-proxy.jar\",\"sparkYarnArchive\":" +
                "\"hdfs://ns1/dtInsight/sparkjars/jars\"}}";
        return componentService.addOrUpdateComponent(clusterVO.getClusterId(), componentConfig, null, "hadoop2", "", "[]", EComponentType.SPARK.getTypeCode(),null,"","");
    }



    public List<Column> getTableColumns(Long sourceId,String tableName,String schemaNme,String dbName){

        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        dataSetInfo.setSourceId(sourceId);
        dataSetInfo.setTableName(tableName);
        dataSetInfo.setDbName(dbName);
        dataSetInfo.setSchemaName(schemaNme);
        return   dataSetInfoService.getTableColumns(dataSetInfo);
    }


    private DataSourceDTO getDataSourceDTO(Long tenantId) {

        String dataJson = "{\"maxJobPoolSize\":\"\",\"password\":\"123\",\"minJobPoolSize\":\"\"," +
                "\"jdbcUrl\":\"jdbc:hive2://172.16.8.107:10000/default\"," +
                "\"username\":\"admin\",\"typeName\":\"hive2.1.1-cdh6.1.1\"}";
//        String kerberosConf = "{\n" +
//                "\"principalFile\":\"hive_pure.keytab\",\n" +
//                "\"remoteDir\":\"/data/sftp_dev/CONSOLE_kerberos/SPARK_THRIFT/kerberos\",\n" +
//                "\"krbName\":\"krb5.conf\",\n" +
//                "\"openKerberos\":true\n" +
//                "}";
        String kerberosConf = "";
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        dataSourceDTO.setAppType(1);
        dataSourceDTO.setDataJson(dataJson);
        dataSourceDTO.setSourceName("测试逻辑数据源1");
        dataSourceDTO.setDtUicTenantId(tenantId);
        dataSourceDTO.setKerberosConf(kerberosConf);
        dataSourceDTO.setSourceType(27);
        return dataSourceDTO;
    }

    private Long addOrUpdateDataSource(Long tenantId) {

        DataSourceDTO dataSourceDTO = getDataSourceDTO(tenantId);
        return dataSourceService.addOrUpdateDataSource(dataSourceDTO);
    }



    private LineageDataSource getDataSourceByIdAndAppType(Long sourceId){

        return dataSourceService.getDataSourceByIdAndAppType(sourceId, 1);

    }


    private LineageDataSetInfo getOneBySourceIdAndDbNameAndTableName(Long sourceId){

        String dbName = "default";
        String tableName = "t1";
        String schemaName = "t1";

        return dataSetInfoService.getOneBySourceIdAndDbNameAndTableName(sourceId, dbName, tableName, schemaName);
    }


    @Test
    public void testIsSameCluster(){

        Boolean sameCluster = clusterService.isSameCluster(1L, Lists.newArrayList(1L));
        Assert.assertTrue(sameCluster);
    }


}
