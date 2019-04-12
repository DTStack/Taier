package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.JarFileInfo;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * 获取插件的工具类
 * 包括获得jar路径,获取plugin的类名称
 * Date: 2017/8/2
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SqlPluginInfo {

    private static final Logger logger = LoggerFactory.getLogger(SqlPluginInfo.class);

    private static final String sqlPluginDirName = "sqlplugin";

    private static final String CORE_JAR = "core.jar";

    private static String SP = File.separator;

    private String localSqlRootJar;

    private String remoteSqlRootDir;

    private SqlPluginInfo(){

    }

    public static SqlPluginInfo create(FlinkConfig flinkConfig){
        SqlPluginInfo pluginInfo = new SqlPluginInfo();
        pluginInfo.init(flinkConfig);
        return pluginInfo;
    }

    private void init(FlinkConfig flinkConfig){
        String remoteSqlPluginDir = getSqlPluginDir(flinkConfig.getRemotePluginRootDir());
        String localSqlPluginDir = getSqlPluginDir(flinkConfig.getFlinkPluginRoot());

        File sqlPluginDirFile = new File(localSqlPluginDir);
        if(!sqlPluginDirFile.exists() || !sqlPluginDirFile.isDirectory()){
            throw new RdosException("not exists flink sql plugin dir:" + localSqlPluginDir + ", please check it!!!");
        }

        setLocalJarRootDir(localSqlPluginDir);
        setRemoteSourceJarRootDir(remoteSqlPluginDir);
    }

    public String getJarFileDirPath(String type){
        String jarPath = localSqlRootJar + SP + type;
        File jarFile = new File(jarPath);

        if(!jarFile.exists()){
            throw new RdosException("不存在路径: " + jarPath);
        }

        return jarPath;
    }

    public void setLocalJarRootDir(String rootDir){

        if(localSqlRootJar != null){
            return;
        }

        localSqlRootJar = rootDir;
        logger.info("---------local sql plugin root dir is:" + rootDir);
    }

    public void setRemoteSourceJarRootDir(String remoteRootDir){

        if(remoteSqlRootDir != null){
            return;
        }

        remoteSqlRootDir = remoteRootDir;
        logger.info("---------remote sql plugin root dir is:" + remoteSqlRootDir);
    }

    public String getSqlPluginDir(String pluginRoot){
        return pluginRoot + SP + sqlPluginDirName;
    }

    public List<String> buildExeArgs(JobClient jobClient, Properties properties) throws IOException {
        List<String> args = Lists.newArrayList();
        args.add("-sql");
        args.add(URLEncoder.encode(jobClient.getSql(), Charsets.UTF_8.name()));

        args.add("-name");
        args.add(jobClient.getJobName());

        args.add("-localSqlPluginPath");
        args.add(localSqlRootJar);

        args.add("-remoteSqlPluginPath");
        args.add(remoteSqlRootDir);

        args.add("-confProp");
        Properties newProperties = jobClient.getConfProperties();
        Map<String, String> map = new HashMap<String, String>((Map) properties);
        for (String key : map.keySet()) {
            if (key.startsWith("taskparams.")){
                newProperties.setProperty(key, map.get(key));
            }
        }
        String confPropStr = PublicUtil.objToString(newProperties);
        confPropStr = URLEncoder.encode(confPropStr, Charsets.UTF_8.name());
        args.add(confPropStr);
        return args;
    }

    public JarFileInfo createCoreJarInfo(){
        JarFileInfo jarFileInfo = new JarFileInfo();
        String jarFilePath  = localSqlRootJar + SP + CORE_JAR;
        jarFileInfo.setJarPath(jarFilePath);
        return jarFileInfo;
    }

}
