package com.dtstack.engine.master.utils;

import com.dtstack.engine.master.AbstractTest;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: newman
 * Date: 2020/12/31 5:13 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestXmlFileUtil extends AbstractTest {


    @Test
    public void testFilterXml(){

        File coreFile = new File(getClass().getClassLoader().getResource("hadoopConf/core-site.xml").getFile());
        File hdfsFile = new File(getClass().getClassLoader().getResource("hadoopConf/hdfs-site.xml").getFile());
        File yarnFile = new File(getClass().getClassLoader().getResource("hadoopConf/yarn-site.xml").getFile());

        List<File> xmlFiles = Lists.newArrayList(coreFile,hdfsFile,yarnFile);
        ArrayList<String> validXml = Lists.newArrayList();
        List<File> files = XmlFileUtil.filterXml(xmlFiles, validXml);
        Assert.assertNotNull(files);
    }

    @Test
    public void testGetFilesFromZip(){

        List<File> filesFromZip = XmlFileUtil.getFilesFromZip(getClass().getClassLoader().getResource("zip/hadoopConf.zip").getFile(),
                getClass().getClassLoader().getResource("zip").getFile(),
                Lists.newArrayList());
        Assert.assertNotNull(filesFromZip);
    }

}
