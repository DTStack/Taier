package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.NodeMachine;
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

import java.util.List;

/**
 * @author basion
 * @Classname NodeMachineServiceTest
 * @Description unit test for NodeMachineService
 * @Date 2020-11-26 16:04:20
 * @Created basion
 */
@PrepareForTest()
public class NodeMachineServiceTest extends AbstractTest {

    @Autowired
    private NodeMachineService nodeMachineService;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }

    @Test
    public void testListByAppType() {
        List<NodeMachine> listByAppType = nodeMachineService.listByAppType("");
        Assert.assertNotNull(listByAppType);
    }

    @Test
    public void testGetByAppTypeAndMachineType() {
        NodeMachine getByAppTypeAndMachineType = nodeMachineService.getByAppTypeAndMachineType("", 0);
        Assert.assertNotNull(getByAppTypeAndMachineType);
    }
}
