package com.dtstack.engine.worker.metric;

import org.junit.Assert;
import org.junit.Test;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/11/26
 */
public class TestMetric {

    @Test
    public void testCalc() {
        SystemResourcesMetricsAnalyzer systemResourcesMetricsAnalyzer = new SystemResourcesMetricsAnalyzer();
        systemResourcesMetricsAnalyzer.instantiateSystemMetrics(3000);
        systemResourcesMetricsAnalyzer.run();

        Assert.assertEquals(systemResourcesMetricsAnalyzer.getMetrics()!=null, true);
    }
}
