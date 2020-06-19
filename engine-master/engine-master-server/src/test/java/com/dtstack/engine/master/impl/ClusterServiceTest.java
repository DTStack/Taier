package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.enums.EComponentScheduleType;
import com.dtstack.engine.master.enums.EComponentType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author yuebai
 * @date 2020-06-04
 */
public class ClusterServiceTest extends AbstractTest {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    private String testClusterName = "testcase";

    public void testCreateCluster() {
        componentService.addOrCheckClusterWithName(testClusterName);
    }

    public ClusterVO testGetClusterByName() {
        ClusterVO dbCluster = clusterService.getClusterByName(testClusterName);
        Assert.assertNotNull(dbCluster);
        return dbCluster;
    }

    /**
     * @see ComponentService#addOrCheckClusterWithName(java.lang.String)
     * @see ComponentService#addOrUpdateComponent(java.lang.Long, java.lang.String, java.util.List, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
     * @see ComponentService#getOne(java.lang.Long)
     * @see ClusterService#getAllCluster()
     * @see ClusterService#getCluster(java.lang.Long, java.lang.Boolean, java.lang.Boolean)
     * @see ClusterService#pageQuery(int, int)
     */
    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetCluster() {
        //创建集群
        testCreateCluster();
        ClusterVO clusterVO = testGetClusterByName();
        Assert.assertNotNull(clusterVO.getClusterId());
        //添加组件 添加引擎
        ComponentVO yarnComponent = testAddComponent(clusterVO);
        Assert.assertNotNull(yarnComponent.getId());
        Component one = componentService.getOne(yarnComponent.getId());
        Assert.assertNotNull(one);
        //校验查询接口
        testGetAllCluster(clusterVO, yarnComponent);
        //页面展示接口
        testPageQuery();
        //点击详情接口
        testGetCluster(clusterVO);
    }

    private void testPageQuery() {
        PageResult<List<ClusterVO>> listPageResult = clusterService.pageQuery(1, 100);
        Assert.assertNotNull(listPageResult);
        List<ClusterVO> data = listPageResult.getData();
        Assert.assertNotNull(data);
        Optional<ClusterVO> pageQueryVo = data.stream().filter(s -> s.getClusterName().equalsIgnoreCase(testClusterName)).findFirst();
        Assert.assertTrue(pageQueryVo.isPresent());
    }

    private void testGetCluster(ClusterVO clusterVO){
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

    private ComponentVO testAddComponent(ClusterVO clusterVO) {
        componentService.addOrUpdateComponent(clusterVO.getClusterId(),"{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"172.16.100.168\",\"username\":\"root\"}",
                null, "hadoop2", "", "[]", EComponentType.SFTP.getTypeCode());
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
                , null, "hadoop2", "", "[]", EComponentType.YARN.getTypeCode());
    }


}
