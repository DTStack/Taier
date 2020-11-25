package com.dtstack.engine.rdbs.tidb;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class TiDBClientTest {

    @InjectMocks
    TiDBClient tiDBClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        tiDBClient.getConnFactory();
    }

}
