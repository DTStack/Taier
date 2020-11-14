package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author yuebai
 * @date 2020-11-05
 */
public class ConsoleServiceTest extends AbstractTest {

    @Autowired
    private ConsoleService consoleService;

    @Test
    public void testOverview(){
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        Collection<Map<String, Object>> dev = consoleService.overview(engineJobCache.getNodeAddress(), "dev");
        Assert.assertNotNull(dev);
        Collection<Map<String, Object>> defaultCluster = consoleService.overview(engineJobCache.getNodeAddress(), "default");
        Assert.assertTrue(CollectionUtils.isEmpty(defaultCluster));

    }
}
