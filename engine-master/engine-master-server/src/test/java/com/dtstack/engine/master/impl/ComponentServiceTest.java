package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.Engine;
import com.dtstack.engine.domain.EngineTenant;
import com.dtstack.engine.master.vo.ComponentMultiVersionVO;
import com.dtstack.engine.master.vo.ComponentVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.dao.EngineTenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.ValueUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yuebai
 * @date 2021-04-06
 */
public class ComponentServiceTest extends AbstractTest {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Test
    @Transactional
    public void testChangeComponent() {

        Cluster cluster = new Cluster();
        cluster.setClusterName("testChangeComponent");
        cluster.setHadoopVersion("");
        clusterDao.insert(cluster);
        Long clusterId = cluster.getId();

        Engine engine = new Engine();
        engine.setClusterId(clusterId);
        engine.setEngineName(MultiEngineType.HADOOP.getName());
        engine.setEngineType(MultiEngineType.HADOOP.getType());
        engineDao.insert(engine);

        Long engineId = engine.getId();
        Component hiveComponent = new Component();
        hiveComponent.setComponentTypeCode(EComponentType.HIVE_SERVER.getTypeCode());
        hiveComponent.setComponentName(EComponentType.HIVE_SERVER.getName());
        hiveComponent.setClusterId(clusterId);
        hiveComponent.setEngineId(engineId);
        hiveComponent.setStoreType(EComponentType.HDFS.getTypeCode());
        hiveComponent.setIsMetadata(1);
        componentDao.insert(hiveComponent);

        try {
            componentService.changeMetadata(hiveComponent.getComponentTypeCode(), false,engineId,1);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }


        Component sparkThriftComponent = new Component();
        sparkThriftComponent.setComponentTypeCode(EComponentType.SPARK_THRIFT.getTypeCode());
        sparkThriftComponent.setComponentName(EComponentType.SPARK_THRIFT.getName());
        sparkThriftComponent.setClusterId(clusterId);
        sparkThriftComponent.setEngineId(engineId);
        sparkThriftComponent.setStoreType(EComponentType.HDFS.getTypeCode());
        sparkThriftComponent.setIsMetadata(0);
        componentDao.insert(sparkThriftComponent);



        componentService.changeMetadata(sparkThriftComponent.getComponentTypeCode(), true,engineId,0);

        Component dbHiveComponent = componentDao.getOne(hiveComponent.getId());
        Assert.assertNotNull(dbHiveComponent);
        Assert.assertEquals(0, (int) dbHiveComponent.getIsMetadata());

        Component dbSparkThriftComponent = componentDao.getOne(sparkThriftComponent.getId());
        Assert.assertNotNull(dbSparkThriftComponent);
        Assert.assertEquals(1, (int) dbSparkThriftComponent.getIsMetadata());

        EngineTenant engineTenant = new EngineTenant();
        engineTenant.setEngineId(engineId);
        engineTenant.setTenantId(ValueUtils.getChangedLong());
        engineTenantDao.insert(engineTenant);

        try {
            componentService.changeMetadata(sparkThriftComponent.getComponentTypeCode(), true,engineId,0);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RdosDefineException);
        }

    }

    @Test
    public void testTwoSpark(){
        ComponentMultiVersionVO componentVO = new ComponentMultiVersionVO();

        ComponentVO spark210 = new ComponentVO();
        spark210.setHadoopVersion("210");

        ComponentVO spark240 = new ComponentVO();
        spark240.setHadoopVersion("240");
        spark240.setIsDefault(true);

        componentVO.setMultiVersion(Lists.newArrayList(spark210,spark240));
        ComponentVO component = componentVO.getComponent(null);

        Assert.assertNotNull(component);
        Assert.assertNotNull(component.getHadoopVersion());
        Assert.assertEquals(component.getHadoopVersion(),"240");

        ComponentVO versionComponent = componentVO.getComponent("210");

        Assert.assertNotNull(versionComponent);
        Assert.assertNotNull(versionComponent.getHadoopVersion());
        Assert.assertEquals(versionComponent.getHadoopVersion(),"210");
    }


}
