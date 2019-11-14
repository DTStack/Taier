package com.dtstack.engine.sparkyarn.sparkext;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;

import java.io.File;

/**
 * 修改carbondata yarn client ---> 修改提交之前的配置包打包
 * corbondata 的hive配置文件名称为:carbon-hive-site.xml
 * Date: 2019/1/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class CarbondataClientExt extends ClientExt{

    private static final String HIVE_CONF_NAME = "hive-site.xml";

    private static final String CARBON_HIVE_CONF_NAME = "carbon-hive-site.xml";

    public CarbondataClientExt(ClientArguments args, Configuration hadoopConf, SparkConf sparkConf) {
        super(args, hadoopConf, sparkConf);
    }

    /***
     * 将hdfs上的配置文件下载到临时目录下
     * @param hadoopConfFiles
     */
    @Override
    public void loadConfFromDir(scala.collection.mutable.HashMap hadoopConfFiles, String confDirName){
        File confDir = new File(confDirName);
        File[] files = confDir.listFiles((dir, name) -> name.endsWith(XML_SUFFIX) || name.endsWith(CONF_SUFFIX));

        for(File file : files){
            String fileName = file.getName();
            if(HIVE_CONF_NAME.equals(file.getName())){
                continue;
            }

            if(CARBON_HIVE_CONF_NAME.equals(file.getName())){
                fileName = HIVE_CONF_NAME;
            }


            hadoopConfFiles.put(fileName, file);
        }

    }

}
