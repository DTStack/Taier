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
        //spark 暂时全部为perjob
        EDeployMode deploy = EDeployMode.PERJOB;
        JSONObject confConfig = clusterConfigJson.getJSONObject(EComponentType.SPARK.getConfName());
        String typeName = confConfig.getString(TYPE_NAME);
        if (!StringUtils.isBlank(typeName)) {
            clusterConfigJson.put(TYPE_NAME_KEY, typeName);
        }
        confConfig = confConfig.getJSONObject(deploy.getMode());
        if (Objects.isNull(confConfig)) {
            throw new RdosDefineException(String.format("Spark Corresponding mode [%s] no information is configured", deploy.name()));
        }

        JSONObject sftpConfig = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
        if (Objects.nonNull(sftpConfig)) {
            String path = "confPath" + File.separator + clusterConfigJson.getString(ConfigConstant.CLUSTER);
            String confHdfsPath = sftpConfig.getString("path") + File.separator + path;
            clusterConfigJson.put("confHdfsPath", confHdfsPath);
        }
        clusterConfigJson.remove(EComponentType.SPARK.getConfName());
        clusterConfigJson.putAll(confConfig);
        return clusterConfigJson;
    }

    @Override
    public EComponentType getComponentTypeCode() {
        return EComponentType.SPARK;
    }
}
