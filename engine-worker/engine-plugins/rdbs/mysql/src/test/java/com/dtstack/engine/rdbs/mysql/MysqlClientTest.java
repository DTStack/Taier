package com.dtstack.engine.rdbs.mysql;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class MysqlClientTest {

    @InjectMocks
    MysqlClient mysqlClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetConnFactory() {
        mysqlClient.getConnFactory();
    }
}
