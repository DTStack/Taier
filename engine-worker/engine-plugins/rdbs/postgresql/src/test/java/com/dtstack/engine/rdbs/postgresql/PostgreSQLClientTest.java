package com.dtstack.engine.rdbs.postgresql;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class PostgreSQLClientTest {

    @InjectMocks
    PostgreSQLClient postgreSQLClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        postgreSQLClient.getConnFactory();
    }

}
