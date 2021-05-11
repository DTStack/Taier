package com.dtstack.engine.lineage.bo;

import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/20 17:27
 * Description: 单测
 * @since 1.0.0
 */
public class RdbmsDataSourceConfigTest {


    @Test
    public void test(){
        RdbmsDataSourceConfig sourceConfig = new RdbmsDataSourceConfig();
        sourceConfig.setJdbc("jdbc:postgresql://172.16.101.246:5432/");
        sourceConfig.setPass("admin");
        sourceConfig.setUser("admin");
        sourceConfig.setKerberosConfig("");
        String configJson = sourceConfig.getConfigJson();
        Assert.assertNotNull(configJson);

    }
}
