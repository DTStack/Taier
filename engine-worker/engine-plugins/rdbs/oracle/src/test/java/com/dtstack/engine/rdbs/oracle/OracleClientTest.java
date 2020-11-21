package com.dtstack.engine.rdbs.oracle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class OracleClientTest {

    @InjectMocks
    OracleClient oracleClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        oracleClient.getConnFactory();
    }
}
