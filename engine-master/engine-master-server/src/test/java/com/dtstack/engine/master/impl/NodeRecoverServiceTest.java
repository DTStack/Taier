package com.dtstack.engine.master.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dtstack.engine.master.AbstractTest;

/**
 * @author basion
 * @Classname NodeRecoverServiceTest
 * @Description unit test for NodeRecoverService
 * @Date 2020-11-26 16:07:28
 * @Created basion
 */
@PrepareForTest()
public class NodeRecoverServiceTest extends AbstractTest {

    @Autowired
    private NodeRecoverService nodeRecoverService;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }

    @Test
    public void testMasterTriggerNode() {
        nodeRecoverService.masterTriggerNode();
    }

    @Test
    public void testRecoverJobCaches() {
        nodeRecoverService.recoverJobCaches();
    }
}
