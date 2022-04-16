package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Objects;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME_KEY;

public class SparkPluginInfoStrategy implements ComponentPluginInfoStrategy {

    @Override
    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, Long clusterId, Integer deployMode) {
        JSONObject pluginInfo;
        //spark 暂时全部为perjob
        EDeployMode deploy = EDeployMode.PERJOB;
        JSONObject confConfig = clusterConfigJson.getJSONObject(EComponentType.SPARK.getConfName());
        pluginInfo = confConfig.getJSONObject(deploy.getMode());
        if (Objects.isNull(pluginInfo)) {
            throw new RdosDefineException(String.format("Corresponding mode [%s] no information is configured", deploy.name()));
        }
        String typeName = confConfig.getString(TYPE_NAME);
        if (!StringUtils.isBlank(typeName)) {
            pluginInfo.put(TYPE_NAME_KEY, typeName);
        }
        JSONObject sftpConfig = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
        if (Objects.nonNull(sftpConfig)) {
            String path = "confPath" + File.separator + clusterConfigJson.getString(ConfigConstant.CLUSTER);
            String confHdfsPath = sftpConfig.getString("path") + File.separator + path;
            pluginInfo.put("confHdfsPath", confHdfsPath);
        }
        clusterConfigJson.remove(EComponentType.SPARK.getConfName());
        clusterConfigJson.putAll(confConfig);
        return pluginInfo;
    }
}
