package com.dtstack.engine.spark160.sparkext;

import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.spark160.sparkyarn.SparkYarnConfig;
import com.dtstack.engine.spark160.sparkyarn.util.FileUtil;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.apache.spark.deploy.yarn.CdhClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class ClientExt extends CdhClient {

    private static final Logger LOG = LoggerFactory.getLogger(ClientExt.class);

    /**是否从本地的环境变量加载*/
    private boolean isLocal = true;

    private SparkYarnConfig sparkYarnConfig;

    private static String userDir = System.getProperty("user.dir");

    private static String tmpHadoopFilePath = userDir + "/tmpHadoopConf";

    private static String XML_SUFFIX = ".xml";
    public static String CONF_SUFFIX = ".conf";

    private SparkConf sparkConf;

    private Configuration hadoopConf;

    public ClientExt(ClientArguments args, Configuration hadoopConf, SparkConf sparkConf) {
        super(args, hadoopConf, sparkConf);
        this.sparkConf = sparkConf;
        this.hadoopConf = hadoopConf;
    }

    public void setSparkYarnConfig(SparkYarnConfig sparkYarnConfig) {
        this.sparkYarnConfig = sparkYarnConfig;
    }

    @Override
    public void loadHadoopConf(scala.collection.mutable.HashMap hadoopConfFiles){
        if(!Strings.isNullOrEmpty(sparkYarnConfig.getConfHdfsPath())){
            isLocal = false;
        }

        if(isLocal){
             loadConfFromLocal(hadoopConfFiles);
             return;
        }else{
            loadHadoopConfFromHdfs(hadoopConfFiles);
        }

    }

    /***
     * 将hdfs上的配置文件下载到临时目录下
     * @param hadoopConfFiles
     */
    private void loadHadoopConfFromHdfs(scala.collection.mutable.HashMap hadoopConfFiles){
        String confDirName = getConfDirName();
        File confDir = new File(confDirName);
        File[] files = confDir.listFiles((dir, name) -> name.endsWith(XML_SUFFIX) || name.endsWith(CONF_SUFFIX));

        for(File file : files){
            String fileName = file.getName();
            hadoopConfFiles.put(fileName, file);
        }

    }

    public String getConfDirName(){
        String confMd5Sum = sparkYarnConfig.getMd5sum();
        String confFileDirName = String.format("%s/%s", tmpHadoopFilePath, confMd5Sum);
        File dirFile = new File(confFileDirName);

        try {
            Files.createParentDirs(dirFile);
        } catch (IOException e) {
            throw new RdosException(String.format("can not create dir '%s' on engine", dirFile.getParent()));
        }

        if(dirFile.exists()){
            File[] files = dirFile.listFiles();
            if (files != null && files.length > 0){
                return confFileDirName;
            }
        } else {
            if(!dirFile.mkdir()){
                throw new RdosException(String.format("can not create dir '%s' on engine", confFileDirName));
            }
        }

        //从hdfs下载文件到新创建的目录下
        String hdfsPath = sparkYarnConfig.getConfHdfsPath();
        try {
            FileUtil.downLoadDirFromHdfs(hdfsPath, confFileDirName, hadoopConf);
        } catch (Exception e){
            LOG.error("", e);
            try {
                //下载失败后文件可能没有成功下载或下载不全，直接删除该目录
                FileUtil.deleteFile(confFileDirName);
            } catch (Exception e1) {
                LOG.error("", e1);
            }
            throw new RuntimeException("----从hdfs下载文件异常---");
        }

        return confFileDirName;
    }

}
