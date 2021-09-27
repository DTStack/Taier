package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.ComponentConfig;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.ValueUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yuebai
 * @date 2021-04-13
 */
public class ComponentMethodTest extends AbstractTest {

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private ComponentService componentService;

    @Test
    public void testRefreshTypeName(){
        Long clusterId = ValueUtils.getChangedLong();
        Long engineId = ValueUtils.getChangedLong();
        Component dbComponent = new Component();
        dbComponent.setClusterId(clusterId);
        dbComponent.setEngineId(engineId);
        dbComponent.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        dbComponent.setHadoopVersion("2.7.3");
        dbComponent.setStoreType(EComponentType.HDFS.getTypeCode());
        dbComponent.setComponentName(EComponentType.YARN.getName());

        Component refreshComponent = new Component();
        refreshComponent.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        refreshComponent.setHadoopVersion("3.1.2");
        refreshComponent.setClusterId(clusterId);
        dbComponent.setEngineId(engineId);



        Component flinkComponent = new Component();
        flinkComponent.setClusterId(clusterId);
        flinkComponent.setEngineId(engineId);
        flinkComponent.setComponentTypeCode(EComponentType.FLINK.getTypeCode());
        flinkComponent.setHadoopVersion("180");
        flinkComponent.setStoreType(EComponentType.HDFS.getTypeCode());
        flinkComponent.setComponentName(EComponentType.FLINK.getName());
        componentDao.insert(flinkComponent);

        Component hdfsComponent = new Component();
        hdfsComponent.setClusterId(clusterId);
        hdfsComponent.setEngineId(engineId);
        hdfsComponent.setComponentTypeCode(EComponentType.HDFS.getTypeCode());
        hdfsComponent.setHadoopVersion("2.7.3");
        hdfsComponent.setStoreType(EComponentType.HDFS.getTypeCode());
        hdfsComponent.setComponentName(EComponentType.HDFS.getName());
        componentDao.insert(hdfsComponent);


        ComponentConfig componentConfig = new ComponentConfig();
        componentConfig.setClusterId(clusterId);
        componentConfig.setComponentId(flinkComponent.getId());
        componentConfig.setComponentTypeCode(EComponentType.FLINK.getTypeCode());
        componentConfig.setType(EFrontType.OTHER.name());
        componentConfig.setKey(ConfigConstant.TYPE_NAME_KEY);
        componentConfig.setValue("yarn2-hdfs2-flink110");
        componentConfig.setRequired(1);
        componentConfigDao.insertBatch(Lists.newArrayList(componentConfig));

        componentService.refreshVersion(EComponentType.YARN,engineId,refreshComponent,dbComponent.getHadoopVersion(),"hadoop3");

        ComponentConfig refreshConfig = componentConfigDao.listByKey(flinkComponent.getId(), ConfigConstant.TYPE_NAME_KEY);
        Assert.assertNotNull(refreshConfig);
        Assert.assertEquals(refreshConfig.getValue(),"yarn3-hdfs3-flink110");
        Component dbHdfsComponent = componentDao.getByEngineIdAndComponentType(engineId, EComponentType.HDFS.getTypeCode());
        Assert.assertNotNull(dbHdfsComponent);
        Assert.assertEquals(dbHdfsComponent.getHadoopVersion(),"3.1.2");
    }
}
