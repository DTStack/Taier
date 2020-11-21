package com.dtstack.engine.rdbs.hive;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class HiveClientTest {

    @InjectMocks
    HiveClient hiveClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        hiveClient.getConnFactory();
    }

}
