package com.dtstack.engine.rdbs.sqlserver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class SqlserverClientTest {

    @InjectMocks
    SqlserverClient sqlserverClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        sqlserverClient.getConnFactory();
    }

}
