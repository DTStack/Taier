package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.schedule.common.util.Xml2JsonUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuebai
 * @date 2021-03-02
 */
public class ScheduleDictServiceTest extends AbstractTest {

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Test
    public void testVersion(){
        Map<String, List<ClientTemplate>> version = scheduleDictService.getVersion();
        Assert.assertNotNull(version);
    }

    @Test
    public void testParseXml(){
        List<ComponentConfig> clientTemplates = scheduleDictService.loadExtraComponentConfig("HDP 3.1.x ", EComponentType.SPARK.getTypeCode());
        Assert.assertNotNull(clientTemplates);
    }

    @Test
    public void testAddConfigToXml() {
        URL resource = this.getClass().getResource("/xml/hive-site.xml");
        File file = new File(resource.getFile());
        try {
            Xml2JsonUtil.xml2map(file);
        } catch (Exception e) {
            Assert.fail();
        }
        Map<String, Object> extraConfig = new HashMap<>();
        extraConfig.put("test1", "test1");
        extraConfig.put("test2", "test2");
        extraConfig.put("test3", "test3");
        extraConfig.put("test4", "test4");
        try {
            Xml2JsonUtil.addInfoIntoXml(file, extraConfig, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
