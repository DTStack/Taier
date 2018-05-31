package com.dtstack.rdos.engine.execution.flink140;

import com.dtstack.rdos.commom.exception.RdosException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
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

    private static String SP = File.separator;

    private static Gson gson = new Gson();

    private String sqlRootDir;

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
        String jarPath = sqlRootDir + SP + type + SP + type + ".jar";
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
        String jsonPath = sqlRootDir + SP + sinkType + SP + sinkType + ".json";
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

        if(sqlRootDir != null){
            return;
        }

        sqlRootDir = rootDir;
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

}
