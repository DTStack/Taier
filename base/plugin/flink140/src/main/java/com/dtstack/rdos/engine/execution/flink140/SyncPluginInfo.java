package com.dtstack.rdos.engine.execution.flink140;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.flink140.util.FlinkUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 插件加载
 * Date: 2018/5/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SyncPluginInfo {

    private static final String sp = File.separator;

    private static final String syncPluginDirName = "syncplugin";

    /**同步数据插件jar名称*/
    private static final String syncJarFileName = "flinkx.jar";

    //同步模块在flink集群加载插件
    private String flinkRemoteSyncPluginRoot;

    //同步模块的monitorAddress, 用于获取错误记录数等信息
    private String monitorAddress;

    public static SyncPluginInfo create(FlinkConfig flinkConfig){
        SyncPluginInfo syncPluginInfo = new SyncPluginInfo();
        return syncPluginInfo;
    }

    public void init(FlinkConfig flinkConfig){
        this.flinkRemoteSyncPluginRoot = getSyncPluginDir(flinkConfig.getRemotePluginRootDir());
        this.monitorAddress = flinkConfig.getMonitorAddress();
    }

    public void loadSyncPlugin(){
        List<URL> classPaths = flinkRemoteSyncPluginRoot != null ?
                FlinkUtil.getUserClassPath(programArgList, flinkRemoteSyncPluginRoot) : new ArrayList<>();
    }

    public List<String> createSyncPluginArgs(JobClient jobClient){

        String args = jobClient.getClassArgs();
        List<String> programArgList = Lists.newArrayList();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        programArgList.add("-monitor");
        if(StringUtils.isNotEmpty(monitorAddress)) {
            programArgList.add(monitorAddress);
        } else {
            programArgList.add(getReqUrl());
        }

        return programArgList;
    }

    public String getSyncPluginDir(String pluginRoot){
        return pluginRoot + sp + syncPluginDirName;
    }
}
