package com.dtstack.engine.flink.util;

import com.dtstack.engine.base.filesystem.FilesystemManager;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.enums.FlinkMode;
import com.dtstack.engine.worker.enums.ClassLoaderType;
import com.dtstack.schedule.common.util.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.JarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkUtil {

    private static final Logger logger = LoggerFactory.getLogger(FlinkUtil.class);

    public final static String USER_DIR = System.getProperty("user.dir");

    private final static String tmpK8sConfigDir = "tmpK8sConf";

    public static PackagedProgram buildProgram(String fromPath, String localDir, List<URL> classpaths, EJobType jobType,
                                               String entryPointClass, String[] programArgs,
                                               SavepointRestoreSettings spSetting, org.apache.flink.configuration.Configuration flinkConfiguration, FilesystemManager filesystemManager)
            throws IOException, ProgramInvocationException {
        if (fromPath == null) {
            throw new IllegalArgumentException("The program JAR file was not specified.");
        }

        File jarFile = downloadJar(fromPath, localDir, filesystemManager, true);

        ClassLoaderType classLoaderType = ClassLoaderType.getClassLoaderType(jobType);
        if (ClassLoaderType.CHILD_FIRST == classLoaderType) {
            flinkConfiguration.setString(CoreOptions.CLASSLOADER_RESOLVE_ORDER, "child-first");
            flinkConfiguration.setString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_FALSE);
        } else if (ClassLoaderType.PARENT_FIRST == classLoaderType) {
            flinkConfiguration.setString(CoreOptions.CLASSLOADER_RESOLVE_ORDER, "parent-first");
            flinkConfiguration.setString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_FALSE);
        } else if (ClassLoaderType.CHILD_FIRST_CACHE == classLoaderType) {
            flinkConfiguration.setString(CoreOptions.CLASSLOADER_RESOLVE_ORDER, "child-first");
            flinkConfiguration.setString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_TRUE);
        } else if (ClassLoaderType.PARENT_FIRST_CACHE == classLoaderType) {
            flinkConfiguration.setString(CoreOptions.CLASSLOADER_RESOLVE_ORDER, "parent-first");
            flinkConfiguration.setString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_TRUE);
        }

        PackagedProgram program = PackagedProgram.newBuilder()
                .setJarFile(jarFile)
                .setUserClassPaths(classpaths)
                .setEntryPointClassName(entryPointClass)
                .setConfiguration(flinkConfiguration)
                .setArguments(programArgs)
                .setSavepointRestoreSettings(spSetting)
                .build();

        return program;
    }

    public static File downloadJar(String remotePath, String localDir, FilesystemManager filesystemManager, boolean localPriority) throws IOException {
        if(localPriority){
            //如果不是http 或者 hdfs协议的从本地读取
            File localFile = new File(remotePath);
            if(localFile.exists()){
                return localFile;
            }
        }

        String localJarPath = FlinkUtil.getTmpFileName(remotePath, localDir);
        File downloadFile = filesystemManager.downloadFile(remotePath, localJarPath);
        logger.info("downloadFile remotePath:{} localJarPath:{}", remotePath, localJarPath);

        URL jarFileUrl;

        try {
            jarFileUrl = downloadFile.getAbsoluteFile().toURI().toURL();
        } catch (MalformedURLException e1) {
            throw new IllegalArgumentException("The jar file path is invalid.");
        }

        JarUtils.checkJarFile(jarFileUrl);

        return downloadFile;
    }

    private static String getTmpFileName(String fileUrl, String toPath){
        String fileName = StringUtils.substringAfterLast(fileUrl, File.separator);
        String tmpFileName = toPath  + File.separator + fileName;
        return tmpFileName;
    }

    /**
     *
     * FIXME 仅针对sql执行方式,暂时未找到区分设置source,transform,sink 并行度的方式
     * 设置job运行的并行度
     * @param properties
     */
    public static int getEnvParallelism(Properties properties){
        String parallelismStr = properties.getProperty(ConfigConstrant.SQL_ENV_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr)?Integer.parseInt(parallelismStr):1;
    }


    /**
     * 针对MR类型整个job的并发度设置
     * @param properties
     * @return
     */
    public static int getJobParallelism(Properties properties){
        String parallelismStr = properties.getProperty(ConfigConstrant.MR_JOB_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr)?Integer.parseInt(parallelismStr):1;
    }


    /**
     * get task run mode
     * @param properties
     * @return
     */
    public static FlinkMode getTaskRunMode(Properties properties, ComputeType computeType){
        String modeStr = properties.getProperty(ConfigConstrant.FLINK_TASK_RUN_MODE_KEY);

        if (StringUtils.isEmpty(modeStr)){
            if (ComputeType.STREAM == computeType) {
                //1.10 不支持perjob的native
                return FlinkMode.PER_JOB;
            } else {
                return FlinkMode.SESSION;
            }
        }
        return FlinkMode.mode(modeStr);
    }

    public static void downloadK8sConfig(Properties prop, FlinkConfig flinkConfig, FilesystemManager filesystemManager) throws IOException {
        String tmpK8sConfig = String.format("%s/%s", USER_DIR, tmpK8sConfigDir);

        String remoteDir = flinkConfig.getRemoteDir();
        String k8sConfigName = flinkConfig.getKubernetesConfigName();
        String md5sum = flinkConfig.getMd5sum();
        String remoteConfigPath = String.format("%s/%s", remoteDir, k8sConfigName);
        String localConfigPath = String.format("%s/%s/%s", tmpK8sConfig, md5sum, k8sConfigName);

        String localConfigParentDir = localConfigPath.substring(0, localConfigPath.lastIndexOf("/"));
        File tmpConfigDir = new File(localConfigParentDir);
        if (!tmpConfigDir.exists()) {
            tmpConfigDir.mkdirs();
        }

        if (!new File(localConfigPath).exists()) {
            filesystemManager.downloadFile(remoteConfigPath, localConfigPath);
            ZipUtil.upzipFile(localConfigPath, localConfigParentDir);
        }

        String configName = getConfigNameFromTmpDir(tmpConfigDir);
        String targetLocalConfigPath = String.format("%s/%s", localConfigParentDir, configName);
        prop.setProperty(KubernetesConfigOptions.KUBE_CONFIG_FILE.key(), targetLocalConfigPath);
    }

    private static String getConfigNameFromTmpDir(File tmpConfigDir) {
        String[] contentFiles = tmpConfigDir.list();
        if (contentFiles.length <= 1) {
            throw new RuntimeException("k8s config file not exist");
        }

        for(String fileName : contentFiles) {
            if (!fileName.endsWith(".zip")) {
                return fileName;
            }
        }
        return null;
    }


}