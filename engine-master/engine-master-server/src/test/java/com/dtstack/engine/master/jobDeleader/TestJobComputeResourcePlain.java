package com.dtstack.engine.master.jobDeleader;

import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.master.AbstractTest;
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
    private ComponentService componentService;

    @Test
    public void testGetJobResource() throws Exception {

        JobClient jobClient = CommonUtils.getJobClient();
        //插入集群数据
        ComponentsResultVO test_cluster = componentService.addOrCheckClusterWithName("test_cluster");


        String jobResource = resourcePlain.getJobResource(jobClient);
        Assert.assertNotNull(jobResource);

    }
}
