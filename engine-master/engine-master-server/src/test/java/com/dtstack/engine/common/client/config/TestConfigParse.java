package com.dtstack.engine.common.client.config;

import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.config.YamlConfigParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-02-05
 */
public class TestConfigParse extends YamlConfigParser {

    @Test
    public void testPassword() {
        try {
            URL resource = this.getClass().getResource("/config/default-config.yaml");
            List<ClientTemplate> config = parse(new FileInputStream(new File(resource.getFile())));
            Assert.assertNotNull(config);
            Assert.assertTrue(config.stream().anyMatch(c -> c.getKey().equalsIgnoreCase("password") &&
                    c.getType().equalsIgnoreCase("password")));
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
