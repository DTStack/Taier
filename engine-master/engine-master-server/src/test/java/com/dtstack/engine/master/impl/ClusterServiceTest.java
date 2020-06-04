package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.master.BaseTest;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-06-04
 */
public class ClusterServiceTest extends BaseTest {

    @Autowired
    private ClusterService clusterService;

    @Test
    public void testPluginInfo(){
        ArrayList<String> engineTypeStrs = Lists.newArrayList("spark", "hadoop", "hive");
        for (String engineTypeStr : engineTypeStrs) {
            String pluginInfo = clusterService.pluginInfo(1L, engineTypeStr, null, null);
            if(!StringUtils.isEmpty(pluginInfo)){
                JSONObject pluginConfig = JSONObject.parseObject(pluginInfo);
                Assert.notNull(pluginConfig.getString("typeName"));
            }
        }
    }

    @Test
    public void testGetAllCluster(){
        List<ClusterEngineVO> allCluster = clusterService.getAllCluster();
        Assert.notEmpty(allCluster);
    }


    @Test
    public void testGetCluster(){
        ClusterVO cluster = clusterService.getCluster(-1L, false, true);
        Assert.notNull(cluster);
    }
}
