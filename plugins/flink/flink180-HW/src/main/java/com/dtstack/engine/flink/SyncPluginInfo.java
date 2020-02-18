package com.dtstack.engine.flink;

import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.flink.enums.FlinkYarnMode;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据同步插件
 * Date: 2018/5/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SyncPluginInfo {

    private static final Logger LOG = LoggerFactory.getLogger(SyncPluginInfo.class);

    public static final String fileSP = File.separator;

    public static final String SYNC_PLUGIN_DIR_NAME = "syncplugin";

    private static final String CORE_JAR_NAME_PREFIX = "flinkx";

    private static final String FILE_PROTOCOL = "file://";

    //同步模块在flink集群加载插件
    private String flinkRemoteSyncPluginRoot;

    private String localSyncFileDir;

    //同步模块的monitorAddress, 用于获取错误记录数等信息
    private String monitorAddress;

    private SyncPluginInfo(){
    }

    public static SyncPluginInfo create(FlinkConfig flinkConfig){
        SyncPluginInfo syncPluginInfo = new SyncPluginInfo();
        syncPluginInfo.init(flinkConfig);
        return syncPluginInfo;
    }

    public void init(FlinkConfig flinkConfig){
        this.flinkRemoteSyncPluginRoot = getSyncPluginDir(flinkConfig.getRemotePluginRootDir());
        this.localSyncFileDir = getSyncPluginDir(flinkConfig.getFlinkPluginRoot());
        this.monitorAddress = flinkConfig.getMonitorAddress();
    }

    public List<URL> getClassPaths(List<String> programArgList){
        return flinkRemoteSyncPluginRoot != null ?
                getUserClassPath(programArgList, flinkRemoteSyncPluginRoot) : new ArrayList<>();
    }

    public List<String> createSyncPluginArgs(JobClient jobClient, FlinkClient flinkClient){
        String args = jobClient.getClassArgs();
        List<String> programArgList = Lists.newArrayList();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(),jobClient.getComputeType());

        programArgList.add("-monitor");
        if(StringUtils.isNotEmpty(monitorAddress)) {
            programArgList.add(monitorAddress);
        } else {
            programArgList.add(flinkClient.getReqUrl(taskRunMode));
        }
        programArgList.add("-mode");
        if (FlinkYarnMode.isPerJob(taskRunMode)){
            programArgList.add("yarnPer");
        }else {
            programArgList.add("yarn");
        }
        return programArgList;
    }

    public JarFileInfo createAddJarInfo(){
        JarFileInfo jarFileInfo = new JarFileInfo();
        String coreJarFileName = getCoreJarFileName();
        String jarFilePath  = localSyncFileDir + fileSP + coreJarFileName;
        jarFileInfo.setJarPath(jarFilePath);
        return jarFileInfo;
    }

    private String getCoreJarFileName (){
        String coreJarFileName = null;
        File pluginDir = new File(localSyncFileDir);
        if (pluginDir.exists() && pluginDir.isDirectory()){
            File[] jarFiles = pluginDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().startsWith(CORE_JAR_NAME_PREFIX) && name.toLowerCase().endsWith(".jar");
                }
            });

            if (jarFiles != null && jarFiles.length > 0){
                coreJarFileName = jarFiles[0].getName();
            }
        }

        if (StringUtils.isEmpty(coreJarFileName)){
            throw new RdosException("Can not find core jar file in path:" + localSyncFileDir);
        }

        return coreJarFileName;
    }

    public String getSyncPluginDir(String pluginRoot){
        return pluginRoot + fileSP + SYNC_PLUGIN_DIR_NAME;
    }

    // 数据同步专用: 获取flink端插件classpath, 在programArgsList中添加engine端plugin根目录
    private List<URL> getUserClassPath(List<String> programArgList, String flinkSyncPluginRoot) {
        List<URL> urlList = new ArrayList<>();
        if(programArgList == null || flinkSyncPluginRoot == null){
            return urlList;
        }
        int i = 0;
        for(; i < programArgList.size() - 1; ++i){
            if("-job".equals(programArgList.get(i)) || "--job".equals(programArgList.get(i))){
                break;
            }
        }
        if(i == programArgList.size() - 1){
            return urlList;
        }

        programArgList.add("-pluginRoot");
        programArgList.add(localSyncFileDir);

        String job = programArgList.get(i + 1);

        try {
            job = java.net.URLDecoder.decode(job, "UTF-8");
            programArgList.set(i + 1, job);
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(job, Map.class);
            LinkedTreeMap jobMap = (LinkedTreeMap) map.get("job");

            List<LinkedTreeMap> contentList = (List<LinkedTreeMap>) jobMap.get("content");
            LinkedTreeMap content = contentList.get(0);
            LinkedTreeMap reader = (LinkedTreeMap) content.get("reader");
            String readerName = (String) reader.get("name");
            LinkedTreeMap writer = (LinkedTreeMap) content.get("writer");
            String writerName = (String) writer.get("name");

            Preconditions.checkArgument(StringUtils.isNotEmpty(readerName), "reader name should not be empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(writerName), "writer ame should not be empty");

            File commonDir = new File(localSyncFileDir + fileSP + "common");
            File readerDir = new File(localSyncFileDir + fileSP + readerName);
            File writerDir = new File(localSyncFileDir + fileSP + writerName);
            urlList.addAll(findJarsInDir(commonDir, FILE_PROTOCOL + flinkSyncPluginRoot + fileSP + "common"));
            urlList.addAll(findJarsInDir(readerDir, FILE_PROTOCOL + flinkSyncPluginRoot + fileSP + readerName));
            urlList.addAll(findJarsInDir(writerDir, FILE_PROTOCOL + flinkSyncPluginRoot + fileSP + writerName));

        } catch (Exception e) {
            LOG.error("", e);
        }
        return urlList;
    }

    private List<URL> findJarsInDir(File dir, String prefix)  throws MalformedURLException {
        List<URL> urlList = new ArrayList<>();

        if(dir.exists() && dir.isDirectory()) {
            File[] jarFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar");
                }
            });

            for(File jarFile : jarFiles) {
                URL url = new URL(prefix + File.separator +  jarFile.getName());
                urlList.add(url);
            }

        }

        return urlList;
    }
}
