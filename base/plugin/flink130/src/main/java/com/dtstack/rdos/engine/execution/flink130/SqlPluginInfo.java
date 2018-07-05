package com.dtstack.rdos.engine.execution.flink130;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.JarFileInfo;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**
 * 获取插件的工具类
 * 包括获得jar路径,获取plugin的类名称
 * Date: 2017/8/2
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SqlPluginInfo {

    private static final Logger logger = LoggerFactory.getLogger(SqlPluginInfo.class);

    private static String SINK_GENER_CLASS_KEY = "className";

    private static final String sqlPluginDirName = "sqlplugin";

    private static final String CORE_JAR = "core.jar";

    private static String SP = File.separator;

    private static Gson gson = new Gson();

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

        setSourceJarRootDir(localSqlPluginDir);
        setRemoteSourceJarRootDir(remoteSqlPluginDir);
    }

    public String getJarFilePath(String type){
        String jarPath = localSqlRootJar + SP + type + SP + type + ".jar";
        File jarFile = new File(jarPath);

        if(!jarFile.exists() || jarFile.isDirectory()){
            throw new RdosException("not correct jar file path " + jarPath);
        }

        return jarPath;
    }

    public String getRemoteJarFilePath(String type){
        String jarPath = remoteSqlRootDir + SP + type + SP + type + ".jar";
        return jarPath;
    }

    public static String getFileURLFormat(String filePath){
        return "file://" + filePath;
    }

    public String getClassName(String sinkType) throws IOException {
        String jsonPath = localSqlRootJar + SP + sinkType + SP + sinkType + ".json";
        File jsonFile = new File(jsonPath);

        if(!jsonFile.exists() || jsonFile.isDirectory()){
            throw new RdosException("not correct json file path " + jsonPath);
        }

        URL jsonUrl = jsonFile.toURI().toURL();
        InputStream inputStream = jsonUrl.openStream();
        Reader rd = new InputStreamReader(inputStream);
        Map<String, String> map = gson.fromJson(rd, Map.class);
        return  map.get(SINK_GENER_CLASS_KEY);
    }

    public void setSourceJarRootDir(String rootDir){

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

    public List<String> buildExeArgs(JobClient jobClient) throws IOException {
        List<String> args = Lists.newArrayList();
        args.add("-sql");
        args.add(jobClient.getSql());

        args.add("-name");
        args.add(jobClient.getJobName());

        args.add("-localSqlPluginPath");
        args.add(localSqlRootJar);

        args.add("-remoteSqlPluginPath");
        args.add(remoteSqlRootDir);

        args.add("-confProp");
        String confPropStr = PublicUtil.objToString(jobClient.getConfProperties());
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

    public String getSqlPluginDir(String pluginRoot){
        return pluginRoot + SP + sqlPluginDirName;
    }

}
