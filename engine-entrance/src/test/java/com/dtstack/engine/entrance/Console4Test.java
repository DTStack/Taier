package com.dtstack.engine.entrance;

import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.master.impl.ClusterService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author yuebai
 * @date 2020-05-08
 */
public class Console4Test extends BaseTest {


    @Autowired
    private ClusterService clusterService;

    @Test
    @Transactional
    @Rollback
    public void TestCreateCluster() {
        String name = "createClusterTest";
        ClusterDTO clusterDTO = new ClusterDTO();
        clusterDTO.setClusterName(name);
        ClusterVO clusterVO = clusterService.addCluster(clusterDTO);

        Assert.notNull(clusterService.getClusterByName(name));
    }


    @Test
    @Transactional
    @Rollback
    public void TestDeleteCluster() {
        clusterService.deleteCluster(19l);
    }

}
