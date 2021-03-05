package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.client.config.YamlConfigParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

public class FlinkConfigParse extends YamlConfigParser {

    @Test
    public void testFlink() {
        try {
            URL resource = this.getClass().getResource("/config/flink-config.yaml");
            List<ClientTemplate> config = parse(new FileInputStream(new File(resource.getFile())));
            Assert.assertNotNull(config);
            Assert.assertEquals(config.size(),1);
            Assert.assertEquals(config.get(0).getValues().size(),3);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
