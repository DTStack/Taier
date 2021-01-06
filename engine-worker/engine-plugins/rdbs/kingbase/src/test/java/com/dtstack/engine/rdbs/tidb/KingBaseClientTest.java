package com.dtstack.engine.rdbs.tidb;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class KingBaseClientTest {

    @InjectMocks
    KingBaseClient kingBaseClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        kingBaseClient.getConnFactory();
    }

}
