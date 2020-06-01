package com.dtstack.engine.flink.plugininfo;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.flink.FlinkConfig;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/04/03
 */
public class SqlPluginInfo {

    private static final Logger logger = LoggerFactory.getLogger(SqlPluginInfo.class);

    private static final String SQLPLUGIN = "sqlplugin";

    private static final String CORE = "core";

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
            throw new RdosDefineException("not exists flink sql plugin dir:" + localSqlPluginDir + ", please check it!!!");
        }

        setLocalJarRootDir(localSqlPluginDir);
        setRemoteSourceJarRootDir(remoteSqlPluginDir);
    }

    public String getJarFileDirPath(String type){
        String jarPath = localSqlRootJar + SP + type;
        File jarFile = new File(jarPath);

        if(!jarFile.exists()){
            throw new RdosDefineException("don't exists path: " + jarPath);
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
        return pluginRoot + SP + SQLPLUGIN;
    }

    public List<String> buildExeArgs(JobClient jobClient) throws IOException {
        List<String> args = Lists.newArrayList();
        args.add("-sql");
        args.add(URLEncoder.encode(jobClient.getSql(), Charsets.UTF_8.name()));

        args.add("-name");
        args.add(jobClient.getJobName());

        args.add("-mode");
        args.add("kubernetes");

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
        String coreJarFileName = getCoreJarFileName();
        String jarFilePath  = localSqlRootJar + File.separator + coreJarFileName;
        jarFileInfo.setJarPath(jarFilePath);
        return jarFileInfo;
    }

    private String getCoreJarFileName (){
        String coreJarFileName = null;
        File pluginDir = new File(localSqlRootJar);
        if (pluginDir.exists() && pluginDir.isDirectory()){
            File[] jarFiles = pluginDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().startsWith(CORE) && name.toLowerCase().endsWith(".jar");
                }
            });

            if (jarFiles != null && jarFiles.length > 0){
                coreJarFileName = jarFiles[0].getName();
            }
        }

        if (StringUtils.isEmpty(coreJarFileName)){
            throw new RdosDefineException("Can not find core jar file in path:" + localSqlRootJar);
        }

        return coreJarFileName;
    }

}
