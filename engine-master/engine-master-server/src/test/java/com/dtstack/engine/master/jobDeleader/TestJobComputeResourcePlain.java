package com.dtstack.engine.master.jobDeleader;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.jobdealer.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 10:06 上午 2020/11/13
 */
public class TestJobComputeResourcePlain extends AbstractTest {


    @Autowired
    private JobComputeResourcePlain resourcePlain;

    @Autowired
    private ClusterDao clusterDao;

    @Test
    public void testGetJobResource() throws Exception {
        Cluster cluster = addDefaultCluster();
        JobClient jobClient = CommonUtils.getJobClient();
        String jobResource = resourcePlain.getJobResource(jobClient);
        Assert.assertNotNull(jobResource);

    }


    
    private Cluster addDefaultCluster(){
        
        Cluster cluster = new Cluster();
        cluster.setId(-1L);
        cluster.setClusterName("defalut");
        cluster.setHadoopVersion("");
        Integer insert = clusterDao.insertWithId(cluster);
        return cluster;
    }
}
