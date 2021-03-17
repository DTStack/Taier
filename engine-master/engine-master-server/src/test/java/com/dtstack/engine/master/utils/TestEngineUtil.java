package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.master.AbstractTest;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @Author: newman
 * Date: 2020/12/31 3:40 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestEngineUtil extends AbstractTest {

    @Test
    public void testIsRequiredComponent(){

        boolean flag = EngineUtil.isRequiredComponent(4);
        Assert.assertTrue(flag);
    }

    @Test
    public void testCheckComponent(){

        EngineUtil.checkComponent(MultiEngineType.HADOOP, Lists.newArrayList(1,2));

    }

    @Test
    public void testClassifyComponent(){

        Map<Integer, List<String>> listMap = EngineUtil.classifyComponent(Sets.newHashSet("libraConf"));
        Assert.assertNotNull(listMap);
    }

    @Test
    public void testGetByType(){

        MultiEngineType byType = EngineUtil.getByType(2);
        Assert.assertEquals(2,byType.getType());
    }

}
