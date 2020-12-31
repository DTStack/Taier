package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.jobdealer.resource.CommonResource;
import com.dtstack.engine.master.jobdealer.resource.ComputeResourceType;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 8:36 下午 2020/11/12
 */
public class TestCommonResource  extends AbstractTest {


    @Autowired
    private CommonResource commonResource;

    @Autowired
    private ClusterDao clusterDao;

    @MockBean
    private EngineDao engineDao;

    @MockBean
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Before
    public void setUp(){
        when(clusterService.getClusterByTenant(any())).thenReturn(new ClusterVO());
        when(engineDao.listByClusterId(any())).thenReturn(new ArrayList<Engine>());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testNewInstance() throws Exception {
        JobClient jobClient = CommonUtils.getJobClient();
        ComputeResourceType resourceType = commonResource.newInstance(jobClient);
        Assert.assertNotNull(resourceType);
        jobClient.setEngineType(EngineType.Spark.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Learning.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.DtScript.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Hadoop.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Hive.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Mysql.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Oracle.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Sqlserver.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Maxcompute.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.PostgreSQL.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Kylin.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Impala.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.TiDB.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Presto.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.GreenPlum.name());
        commonResource.newInstance(jobClient);
        jobClient.setEngineType(EngineType.Dummy.name());
        commonResource.newInstance(jobClient);

    }




    @Test
    public void testGetSetClusterDao(){

        commonResource.setClusterDao(clusterDao);
        ClusterDao clusterDao = commonResource.getClusterDao();
        Assert.assertNotNull(clusterDao);
    }


    @Test
    public void testGetSetEngineDao(){

        commonResource.setEngineDao(engineDao);
        EngineDao engineDao = commonResource.getEngineDao();
        Assert.assertNotNull(engineDao);
    }

    @Test
    public void testGetSetClusterService(){

        commonResource.setClusterService(clusterService);
        ClusterService clusterService = commonResource.getClusterService();
        Assert.assertNotNull(clusterService);
    }


    @Test
    public void testGetSetComponentService(){

        commonResource.setComponentService(componentService);
        ComponentService componentService = commonResource.getComponentService();
        Assert.assertNotNull(componentService);
    }
}
