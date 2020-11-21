package com.dtstack.engine.rdbs.impala;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class ImpalaClientTest {

    @InjectMocks
    ImpalaClient impalaClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        impalaClient.getConnFactory();
    }

}
