package com.dtstack.engine.flink.storage;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkConfig;
import com.sun.xfile.XFile;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.utils.Constants;
import org.apache.flink.util.Preconditions;

import java.util.Map;
import java.util.Properties;

public class NfsStorage extends AbstractStorage {

    private static final String VOLUME_TYPE = "nfs";
    private static final String KEY_NFS_SERVICE = "service";
    private static final String KEY_NFS_PATH = "path";

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

        // set flinkplugin volume
        String remotePlugin = flinkConfig.getRemotePluginRootDir();
        checkReadPermission(remotePlugin);
        String dockerPluginHome = config.get(KubernetesConfigOptions.KUBERNETES_DOCKER_FLINKPLUGIN_PATH);
        setNfsMountConf(config, "flinkplugin", remotePlugin, dockerPluginHome);
        flinkConfig.setRemotePluginRootDir(dockerPluginHome);

        // set haPath volume
        String haStoragePath = config.getString(HighAvailabilityOptions.HA_STORAGE_PATH);
        String remoteHaStoragePath = createDirOnNfs(haStoragePath);
        String dockerHaStoragePath = config.get(KubernetesConfigOptions.KUBERNETES_DOCKER_HA_STORAGE_PATH);
        setNfsMountConf(config, "hastorage", remoteHaStoragePath, dockerHaStoragePath);
        config.setString(HighAvailabilityOptions.HA_STORAGE_PATH, "file://" + dockerHaStoragePath);

        // set completed-jobs volume
        String completedJobs = config.getString(JobManagerOptions.ARCHIVE_DIR);
        String remoteCompletedJobs = createDirOnNfs(completedJobs);
        String dockerCompletedJobs = config.get(KubernetesConfigOptions.KUBERNETES_DOCKER_COMPLETEDJOB_PATH);
        setNfsMountConf(config, "completedjobs", remoteCompletedJobs, dockerCompletedJobs);
        config.setString(JobManagerOptions.ARCHIVE_DIR, "file://" + dockerCompletedJobs);

        // set checkpoints volume
        String checkpointPath = config.getString(CheckpointingOptions.CHECKPOINTS_DIRECTORY);
        String remoteCheckpointPath = createDirOnNfs(checkpointPath);
        String dockerCheckpointPath = config.get(KubernetesConfigOptions.KUBERNETES_DOCKER_CHECKPOINT_PATH);
        setNfsMountConf(config, "checkpoints", remoteCheckpointPath, dockerCheckpointPath);
        config.setString(CheckpointingOptions.CHECKPOINTS_DIRECTORY, "file://" + dockerCheckpointPath);

        // set savepoints volume
        String savepointPath = config.getString(CheckpointingOptions.SAVEPOINT_DIRECTORY);
        String remoteSavepointPath = createDirOnNfs(savepointPath);
        String dockerSavepointPath = config.get(KubernetesConfigOptions.KUBERNETES_DOCKER_SAVEPOINT_PATH);
        setNfsMountConf(config, "savepoints", remoteSavepointPath, dockerSavepointPath);
        config.setString(CheckpointingOptions.SAVEPOINT_DIRECTORY, "file://" + dockerSavepointPath);
    }

    private void setNfsMountConf(Configuration config, String volumeName, String path, String mountPath) {
        // set jobmanager volume
        String jobmanagerVolumeServer = String.format("%s%s.%s.options.server", Constants.KUBERNETES_JOB_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, volumeName);
        config.setString(jobmanagerVolumeServer, service);
        String jobmanagerVolumePath = String.format("%s%s.%s.options.path", Constants.KUBERNETES_JOB_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, volumeName);
        config.setString(jobmanagerVolumePath, path);

        // set jobmanager mount
        String jobmanagerMountPath = String.format("%s%s.%s.mount.mountPath", Constants.KUBERNETES_JOB_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, volumeName);
        config.setString(jobmanagerMountPath, mountPath);

        // set taskmanager volume
        String taskmanagerVolumeServer = String.format("%s%s.%s.options.server", Constants.KUBERNETES_TASK_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, volumeName);
        config.setString(taskmanagerVolumeServer, service);
        String taskmanagerVolumePath = String.format("%s%s.%s.options.path", Constants.KUBERNETES_TASK_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, volumeName);
        config.setString(taskmanagerVolumePath, path);

        // set taskmanager mount
        String taskmanagerMountPath = String.format("%s%s.%s.mount.mountPath", Constants.KUBERNETES_TASK_MANAGER_VOLUMES_PREFIX, VOLUME_TYPE, volumeName);
        config.setString(taskmanagerMountPath, mountPath);
    }

    private String createDirOnNfs(String path) {
        String fileUrl = "";
        if (StringUtils.startsWith(path, "nfs://")) {
            fileUrl = path;
        } else {
            fileUrl = String.format("nfs://%s/%s", service, path);
        }
        XFile nfsFile = new XFile(fileUrl);
        if (!nfsFile.exists()) {
            nfsFile.mkdirs();
        }
        return "/"+ nfsFile.getPath();
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
