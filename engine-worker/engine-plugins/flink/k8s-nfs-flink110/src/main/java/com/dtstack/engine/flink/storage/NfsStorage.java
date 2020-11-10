package com.dtstack.engine.flink.storage;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkConfig;
import com.sun.xfile.XFile;
import org.apache.commons.collections.MapUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.utils.Constants;
import org.apache.flink.util.Preconditions;

import java.util.Map;
import java.util.Properties;

public class NfsStorage extends AbstractStorage {

    private static final String VOLUME_TYPE = "nfs";
    private static final String KEY_NFS_SERVICE = "service";
    private static final String KEY_NFS_PATH = "path";

    private XFile nfsFile;
    private String service;
    private String rootPath;

    @Override
    public void init(Properties pluginInfo) {
        Map<String, Object> nfsConf = (Map<String, Object>) pluginInfo.get("nfsConf");
        if (nfsConf == null || nfsConf.size() == 0) {
            throw new RdosDefineException("No set nfs config!");
        }
        service = MapUtils.getString(nfsConf, KEY_NFS_SERVICE);
        rootPath = MapUtils.getString(nfsConf, KEY_NFS_PATH);
        Preconditions.checkNotNull(service, "no set nfs service");
        Preconditions.checkNotNull(rootPath, "no set nfs path");
        checkReadPermission(rootPath);
        checkWritePermission(rootPath);
    }

    @Override
    public void fillStorageConfig(Configuration config, FlinkConfig flinkConfig) {
        String remotePlugin = flinkConfig.getRemotePluginRootDir();
        checkReadPermission(remotePlugin);
        String dockerPluginHome = config.get(KubernetesConfigOptions.KUBERNETES_DOCKER_PLUGIN_HOME);

        // set jobmanager plugin volume
        String jobmanagerVolumeServer = String.format("%s.%s.%s.options.server", Constants.KUBERNETES_JOB_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, "flinkplugin");
        config.setString(jobmanagerVolumeServer, service);
        String jobmanagerVolumePath = String.format("%s.%s.%s.options.path", Constants.KUBERNETES_JOB_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, "flinkplugin");
        config.setString(jobmanagerVolumePath, remotePlugin);

        // set jobmanager plugin mount
        String jobmanagerMountPath = String.format("%s.%s.%s.mount.path", Constants.KUBERNETES_JOB_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, "flinkplugin");
        config.setString(jobmanagerMountPath, dockerPluginHome);

        // set taskmanager plugin volume
        String taskmanagerVolumeServer = String.format("%s.%s.%s.options.server", Constants.KUBERNETES_TASK_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, "flinkplugin");
        config.setString(taskmanagerVolumeServer, service);
        String taskmanagerVolumePath = String.format("%s.%s.%s.options.path", Constants.KUBERNETES_TASK_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, "flinkplugin");
        config.setString(taskmanagerVolumePath, remotePlugin);

        // set taskmanager plugin mount
        String taskmanagerMountPath = String.format("%s.%s.%s.mount.path", Constants.KUBERNETES_TASK_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, "flinkplugin");
        config.setString(taskmanagerMountPath, dockerPluginHome);

        flinkConfig.setRemotePluginRootDir(dockerPluginHome);
    }

    @Override
    public <T> T getStorageConfig() {
        return null;
    }

    private void checkReadPermission(String path) {
        String url = String.format("nfs://%s/%s", service, path);
        XFile nfsFile = new XFile(url);
        if (!nfsFile.exists()) {
            throw new RdosDefineException("file or dir not exists. path: " + path);
        } else {
            if (!nfsFile.canRead()) {
                throw new RdosDefineException("No read permission. path: " + path);
            }
        }
    }

    private void checkWritePermission(String path) {
        String url = String.format("nfs://%s/%s", service, path);
        XFile nfsFile = new XFile(url);
        if (!nfsFile.exists()) {
            throw new RdosDefineException("file or dir not exists. path: " + path);
        } else {
            if (!nfsFile.canWrite()) {
                throw new RdosDefineException("No write permission. path: " + path);
            }
        }
    }
}
