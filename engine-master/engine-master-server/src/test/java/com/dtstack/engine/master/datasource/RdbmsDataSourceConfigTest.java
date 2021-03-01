package com.dtstack.engine.master.datasource;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author chener
 * @Classname RdbmsDataSourceConfigTest
 * @Description
 * @Date 2020/10/26 9:53
 * @Created chener@dtstack.com
 */
public class RdbmsDataSourceConfigTest {
    @Test
    public void testGetConfigJson(){
        RdbmsDataSourceConfig dataSourceConfig = new RdbmsDataSourceConfig();
        dataSourceConfig.setJdbc("jdbc:hive2://host:port/db?para1=xx");
        dataSourceConfig.setUser("user");
        dataSourceConfig.setPass("pass");
        String configJson = dataSourceConfig.getConfigJson();
        Assert.assertNotNull(configJson);
    }
}
