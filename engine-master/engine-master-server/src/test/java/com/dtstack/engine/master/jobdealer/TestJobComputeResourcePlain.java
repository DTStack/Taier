package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.EngineTenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.jobdealer.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 10:06 上午 2020/11/13
 */
public class TestJobComputeResourcePlain extends AbstractTest {


    @Autowired
    private JobComputeResourcePlain resourcePlain;

    @MockBean
    private EngineTenantDao engineTenantDao;

    @MockBean
    private ClusterDao clusterDao;

    @MockBean
    private ClusterService clusterService;

    @Before
    public void setUp(){
        Cluster cluster = new Cluster();
        cluster.setClusterName("test1");
        when(engineTenantDao.getClusterIdByTenantId(any())).thenReturn(1L);
        when(clusterDao.getOne(any())).thenReturn(cluster);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetJobResource() throws Exception {


        JobClient jobClient = CommonUtils.getJobClient();
        String jobResource = resourcePlain.getJobResource(jobClient);
        Assert.assertNotNull(jobResource);

    }


    
//    private Cluster addDefaultCluster(){
//
//        Cluster cluster = new Cluster();
//        cluster.setId(-1L);
//        cluster.setClusterName("defalut");
//        cluster.setHadoopVersion("");
//        Integer insert = clusterDao.insertWithId(cluster);
//        return cluster;
//    }
}
