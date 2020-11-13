package com.dtstack.engine.master.jobDeleader;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.jobdealer.resource.CommonResource;
import com.dtstack.engine.master.jobdealer.resource.ComputeResourceType;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 8:36 下午 2020/11/12
 */
public class TestCommonResource  extends AbstractTest {


    @Autowired
    private CommonResource commonResource;


    @Test
    public void testNewInstance() throws Exception {

        ComputeResourceType resourceType = commonResource.newInstance(CommonUtils.getJobClient());
        Assert.assertNotNull(resourceType);
    }

}
