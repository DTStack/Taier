package com.dtstack.engine.rdbs.greenplum;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class GreenPlumClientTest {

    @InjectMocks
    GreenPlumClient greenPlumClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        greenPlumClient.getConnFactory();
    }

}
