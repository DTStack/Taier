package com.dtstack.engine.master.utils;

import com.dtstack.engine.master.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * @Author: newman
 * Date: 2020/12/31 4:06 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestFileUtil extends AbstractTest {

    @Test
    public void test() throws FileNotFoundException {

        String contentFromFile = FileUtil.getContentFromFile(getClass().getClassLoader().getResource("json/b.json").getFile());
        Assert.assertNotNull(contentFromFile);
    }

}
