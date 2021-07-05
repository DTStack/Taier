package com.dtstack.batch.sync.handler;

import com.dtstack.batch.common.exception.RdosDefineException;
import org.junit.Test;

public class SyncBuilderFactoryTest {

    private final SyncBuilderFactory syncBuilderFactory = new SyncBuilderFactory();

    @Test(expected = RdosDefineException.class)
    public void initTest() {
        syncBuilderFactory.init();
    }

    @Test(expected = RdosDefineException.class)
    public void getSyncBuilderTest() {
        syncBuilderFactory.getSyncBuilder(1);
    }
}
