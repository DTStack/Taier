package com.dtstack.engine.master.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.dtstack.engine.master.AbstractTest;

/**
 * @author basion
 * @Classname NodeRecoverServiceTest
 * @Description unit test for NodeRecoverService
 * @Date 2020-11-26 16:07:28
 * @Created basion
 */
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
