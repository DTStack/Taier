package com.dtstack.engine.rdbs.inceptor;

import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.junit.Test;
import org.springframework.util.Assert;

public class InceptorClientTest {

    @Test
    public void testGetConnFactory() {
        InceptorClient inceptorClient = new InceptorClient();
        AbstractConnFactory connFactory = inceptorClient.getConnFactory();
        Assert.isInstanceOf(InceptorConnFactory.class, connFactory);
    }
}