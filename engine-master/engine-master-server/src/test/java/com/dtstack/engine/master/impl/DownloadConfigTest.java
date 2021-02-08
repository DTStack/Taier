package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.enums.EComponentType;
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

    @Before
    public void init() {
        Component sftp = new Component();
        sftp.setComponentName("sftp");
        sftp.setComponentConfig("{\"path\":\"/data/sftp\",\"password\":\"123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"127.0.0.1\",\"username\":\"root\"}");
        when(componentDao.getByClusterIdAndComponentType(anyLong(), anyInt())).thenReturn(sftp);
        Component component = new Component();
        component.setComponentName("oracle");
        component.setComponentTypeCode(EComponentType.ORACLE_SQL.getTypeCode());
        component.setComponentConfig("{\"maxJobPoolSize\":\"\",\"password\":\"test\",\"minJobPoolSize\":\"\",\"jdbcUrl\":\"jdbc:oracle:thin:@//127.0.0.1:1521/xe\",\"username\":\"system\"}");
        when(componentDao.getOne(100L)).thenReturn(component);
        Cluster cluster = new Cluster();
        cluster.setClusterName("12");
        when(clusterDao.getOne(any())).thenReturn(cluster);
    }


    @Test
    public void testDownloadConfig() {
        File file = componentService.downloadFile(100L, DownloadType.Config.getCode(), EComponentType.ORACLE_SQL.getTypeCode(), "", "");
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
