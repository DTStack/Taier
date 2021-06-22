package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2021-02-07
 */
public class DownloadConfigTest extends AbstractTest {

    @MockBean
    private ComponentDao componentDao;

    @MockBean
    private ClusterDao clusterDao;

    @Autowired
    private ComponentService componentService;

    @MockBean
    private ComponentConfigService componentConfigService;

    @Before
    public void init() {
        Component sftp = new Component();
        sftp.setComponentName("sftp");
        when(componentDao.getByClusterIdAndComponentType(anyLong(), anyInt(),any())).thenReturn(sftp);
        Component component = new Component();
        component.setComponentName("oracle");
        component.setComponentTypeCode(EComponentType.ORACLE_SQL.getTypeCode());
        when(componentDao.getOne(100L)).thenReturn(component);
        Cluster cluster = new Cluster();
        cluster.setClusterName("12");
        when(clusterDao.getOne(any())).thenReturn(cluster);

        Map sftpMap = JSONObject.parseObject("{\"path\":\"/data/sftp\",\"password\":\"123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"127.0.0.1\",\"username\":\"root\"}", Map.class);
        when(componentConfigService.convertComponentConfigToMap(anyLong(),anyBoolean())).thenReturn(sftpMap);
    }


    @Test
    public void testDownloadConfig() {
        File file = componentService.downloadFile(100L, DownloadType.Config.getCode(), EComponentType.ORACLE_SQL.getTypeCode(), "", "",null);
        Assert.assertNotNull(file);
        try {
            String contentFromFile = FileUtil.getContentFromFile(file.getPath());
            JSONObject fileConfig = JSONObject.parseObject(contentFromFile);
            Assert.assertNotNull(fileConfig);
            Assert.assertTrue(StringUtils.isBlank(fileConfig.getString("password")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
