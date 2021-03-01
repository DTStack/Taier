package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.vo.Pair;
import com.dtstack.engine.common.client.config.YamlConfigParser;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.TypeNameDefaultTemplateUtils;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-02-24
 */
public class TemplateToComponentInitTest extends AbstractTest {

    long templateInitCluster = -2L;


    @Autowired
    private ComponentConfigService componentConfigService;


    @Test
    public void initData() {
        try {
            for (String key : TypeNameDefaultTemplateUtils.typeNameMapping.keySet()) {
                Pair<Long, Integer> pair = TypeNameDefaultTemplateUtils.typeNameMapping.get(key);
                URL resource = this.getClass().getResource(String.format("/config/%s.yaml", key));
                if (null == resource || StringUtils.isBlank(resource.getFile())) {
                    continue;
                }
                File file = new File(resource.getFile());
                InputStream is = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
                List<ClientTemplate> clientTemplates = new YamlConfigParser().parse(is);
                List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, templateInitCluster, pair.getKey(), null, null, pair.getValue());
                componentConfigService.batchSaveComponentConfig(componentConfigs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testVersion() {
        ClientTemplate clientTemplate1 = new ClientTemplate("hadoop2", "hadoop2");
        ClientTemplate clientTemplate2 = new ClientTemplate("hdp5.1.x", "hadoop2");
        ClientTemplate clientTemplate3 = new ClientTemplate("cdh5.1.x", "hadoop2");
        ClientTemplate hadoop2 = new ClientTemplate("hadoop2", Lists.newArrayList(clientTemplate1, clientTemplate2, clientTemplate3));

        ClientTemplate clientTemplate4 = new ClientTemplate("hadoop3", "hadoop3");
        ClientTemplate clientTemplate5 = new ClientTemplate("hdp6.1.x", "hadoop3");
        ClientTemplate clientTemplate6 = new ClientTemplate("cdh6.1.x", "hadoop3");
        ClientTemplate hadoop3 = new ClientTemplate("hadoop3", Lists.newArrayList(clientTemplate4, clientTemplate5, clientTemplate6));

        ClientTemplate hw = new ClientTemplate("huawei", "hw");

        ArrayList<ClientTemplate> clientTemplates = Lists.newArrayList(hadoop2, hadoop3, hw);
        JSONArray.toJSONString(clientTemplates);
    }


    @Test
    public void initSftp() {
        try {
            URL resource = this.getClass().getResource(String.format("/config/%s.yaml", "sftp"));
            if (null == resource || StringUtils.isBlank(resource.getFile())) {
                return;
            }
            File file = new File(resource.getFile());
            InputStream is = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
            List<ClientTemplate> clientTemplates = new YamlConfigParser().parse(is);
            List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, templateInitCluster, 111l, null, null, 10);
            componentConfigService.batchSaveComponentConfig(componentConfigs);
            Assert.assertNotNull(clientTemplates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
