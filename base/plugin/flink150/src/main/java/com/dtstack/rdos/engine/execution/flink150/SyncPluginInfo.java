package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.JarFileInfo;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.flink150.enums.FlinkYarnMode;
import com.dtstack.rdos.engine.execution.flink150.util.FlinkUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * 数据同步插件
 * Date: 2018/5/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SyncPluginInfo {

    private static final Logger LOG = LoggerFactory.getLogger(SyncPluginInfo.class);

    private static final String fileSP = File.separator;

    private static final String syncPluginDirName = "syncplugin";

    private static final String coreJarNamePrefix = "flinkx";

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
        this.localSyncFileDir = getSyncPluginDir(flinkConfig.getFlinkPluginRoot());
        this.monitorAddress = flinkConfig.getMonitorAddress();
    }

    public List<String> createSyncPluginArgs(JobClient jobClient, FlinkClient flinkClient){
        String args = jobClient.getClassArgs();
        List<String> programArgList = Lists.newArrayList();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        programArgList.add("-monitor");
        if(StringUtils.isNotEmpty(monitorAddress)) {
            programArgList.add(monitorAddress);
        } else {
            FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(),jobClient.getComputeType());
            programArgList.add(flinkClient.getReqUrl(taskRunMode));
        }

        programArgList.add("-pluginRoot");
        programArgList.add(localSyncFileDir);

        addJobArg(programArgList);

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
                    return name.toLowerCase().startsWith(coreJarNamePrefix) && name.toLowerCase().endsWith(".jar");
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
        return pluginRoot + fileSP + syncPluginDirName;
    }

    /**
     * 添加 job 的信息
     */
    private void addJobArg(List<String> programArgList) {
        int i = 0;
        for(; i < programArgList.size() - 1; ++i){
            if(programArgList.get(i).equals("-job") || programArgList.get(i).equals("--job")) {
                break;
            }
        }

        if(i == programArgList.size() - 1) {
            return;
        }

        String job = programArgList.get(i + 1);
        try {
            job = java.net.URLDecoder.decode(job, "UTF-8");
            programArgList.set(i + 1, job);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }
}
