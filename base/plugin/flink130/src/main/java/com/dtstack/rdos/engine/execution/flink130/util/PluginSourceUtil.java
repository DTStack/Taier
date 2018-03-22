package com.dtstack.rdos.engine.execution.flink130.util;

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
 * @ahthor xuchao
 */

public class PluginSourceUtil {

    private static final Logger logger = LoggerFactory.getLogger(PluginSourceUtil.class);

    private static String SINK_GENER_CLASS_KEY = "className";

    private static String sqlRootDir;

    private static String remoteSqlRootDir;

    public static String SP = File.separator;

    public static Gson gson = new Gson();

    public static String getJarFilePath(String type){
        String jarPath = sqlRootDir + SP + type + SP + type + ".jar";
        File jarFile = new File(jarPath);

        if(!jarFile.exists() || jarFile.isDirectory()){
            throw new RdosException("not correct jar file path " + jarPath);
        }

        return jarPath;
    }

    public static String getRemoteJarFilePath(String type){
        String jarPath = remoteSqlRootDir + SP + type + SP + type + ".jar";
        return jarPath;
    }

    public static String getFileURLFormat(String filePath){
        return "file://" + filePath;
    }

    public static String getClassName(String sinkType) throws IOException {
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

    public synchronized static void setSourceJarRootDir(String rootDir){

        if(sqlRootDir != null){
            return;
        }

        sqlRootDir = rootDir;
        logger.info("---------local sql plugin root dir is:" + rootDir);
    }

    public synchronized static void setRemoteSourceJarRootDir(String remoteRootDir){

        if(remoteSqlRootDir != null){
            return;
        }

        remoteSqlRootDir = remoteRootDir;
        logger.info("---------remote sql plugin root dir is:" + remoteSqlRootDir);
    }
}
