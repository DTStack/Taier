package com.dtstack.engine.master.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.dtstack.engine.master.AbstractTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testMasterTriggerNode() {
        nodeRecoverService.masterTriggerNode();
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testRecoverJobCaches() {
        nodeRecoverService.recoverJobCaches();
    }
}
