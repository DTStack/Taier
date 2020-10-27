package com.dtstack.engine.sparkk8s.utils;

import com.dtstack.engine.base.filesystem.FilesystemManager;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.sparkk8s.config.SparkK8sConfig;
import com.dtstack.schedule.common.util.Xml2JsonUtil;
import com.dtstack.schedule.common.util.ZipUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.k8s.Config;
import org.apache.spark.deploy.k8s.ExtendConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by sishu.yss on 2018/3/9.
 */
public class SparkConfigUtil {

    private static Logger LOG = LoggerFactory.getLogger(SparkConfigUtil.class);

    private static final String KEY_PRE_STR = "spark.";

    private static final String DEFAULT_FILE_FORMAT = "orc";

    private static final String SESSION_CONF_KEY_PREFIX = "session.";

    private static final String KEY_DEFAULT_FILE_FORMAT = "hive.default.fileformat";

    public final static String USER_DIR = System.getProperty("user.dir");

    private final static String tmpK8sConfigDir = "tmpK8sConf";

    private final static String tmpHadoopFilePath = "/tmpHadoopConf";
    private final static String EXECUTOR_HADOOP_USER_NAME = "spark.executorEnv.HADOOP_USER_NAME";

    public static SparkConf buildBasicSparkConf(Properties sparkDefaultProp) {
        SparkConf sparkConf = new SparkConf();
        // no  wait
        sparkConf.set(Config.WAIT_FOR_APP_COMPLETION(), "false");
        sparkConf.set("deploy-mode", "cluster");

        if (sparkDefaultProp != null) {
            sparkDefaultProp.stringPropertyNames()
                    .stream()
                    .filter(key -> key.toString().contains("."))
                    .forEach(key -> sparkConf.set(key, sparkDefaultProp.getProperty(key).toString()));
        }
        return sparkConf;
    }

    public static void replaceBasicSparkConf(SparkConf sparkConf, Properties confProperties) {
        if (!Objects.isNull(confProperties)) {
            for (Map.Entry<Object, Object> param : confProperties.entrySet()) {
                String key = (String) param.getKey();
                String val = (String) param.getValue();
                if (!key.contains(KEY_PRE_STR)) {
                    key = KEY_PRE_STR + key;
                }
                sparkConf.set(key, val);
            }
        }
    }

    public static Map<String, String> getSparkSessionConf(Properties confProp) {
        Map<String, String> map = Maps.newHashMap();
        map.put(KEY_DEFAULT_FILE_FORMAT, DEFAULT_FILE_FORMAT);

        confProp.stringPropertyNames()
                .stream()
                .filter(key -> key.startsWith(SESSION_CONF_KEY_PREFIX))
                .forEach(key -> {
                    String value = confProp.getProperty(key);
                    key = key.replaceFirst("session\\.", "");
                    map.put(key, value);
                });

        return map;
    }

    public static void buildHadoopSparkConf(SparkConf sparkConf, String hdfsConfPath) {
        Collection<File> files = FileUtils.listFiles(new File(hdfsConfPath), new String[]{"xml"}, false);
        if (files.size() == 0) {
            throw new RdosDefineException("spark sql job hadoop conf is required!");
        }

        files.stream().forEach(file -> {
            try {
                Map<String, Object> stringObjectMap = Xml2JsonUtil.xml2map(file);
                sparkConf.set(file.getName(), SparkConfigUtil.getHdfsContent(stringObjectMap));
            } catch (Exception e) {
                throw new RdosDefineException("get content from xmlFile error! ", e);
            }
        });
    }

    private static String getHdfsContent(Map hadoopConfMap) {
        StringBuilder hadoopConfContent = new StringBuilder();
        hadoopConfContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(System.lineSeparator());
        hadoopConfContent.append("<?xml-stylesheet href=\"configuration.xsl\" type=\"text/xsl\"?>").append(System.lineSeparator());
        hadoopConfContent.append("<configuration>").append(System.lineSeparator());
        Iterator<Map.Entry<String, Object>> it = hadoopConfMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> e = it.next();
            String name = e.getKey();
            String value = e.getValue().toString();
            hadoopConfContent.append("    <property>").append(System.lineSeparator());
            hadoopConfContent.append("        <name>").append(name).append("</name>").append(System.lineSeparator());
            hadoopConfContent.append("        <value>").append(value).append("</value>").append(System.lineSeparator());
            hadoopConfContent.append("    </property>").append(System.lineSeparator());
        }
        hadoopConfContent.append("</configuration>").append(System.lineSeparator());

        return hadoopConfContent.toString();
    }

    public static String downloadK8sConfig(FilesystemManager filesystemManager, SparkK8sConfig sparkK8sConfig) {
        String tmpK8sConfig = String.format("%s/%s", USER_DIR, tmpK8sConfigDir);

        String remoteDir = sparkK8sConfig.getRemoteDir();
        String k8sConfigName = sparkK8sConfig.getKubernetesConfigName();
        String md5sum = sparkK8sConfig.getMd5sum();
        String remoteConfigPath = String.format("%s/%s", remoteDir, k8sConfigName);
        String localConfigPath = String.format("%s/%s/%s", tmpK8sConfig, md5sum, k8sConfigName);

        String localConfigParentDir = localConfigPath.substring(0, localConfigPath.lastIndexOf("/"));
        File tmpConfigDir = new File(localConfigParentDir);
        if (!tmpConfigDir.exists()) {
            tmpConfigDir.mkdirs();
        }

        if (!new File(localConfigPath).exists()) {
            filesystemManager.downloadFile(remoteConfigPath, localConfigPath, false, false, true);

            ZipUtil.upzipFile(localConfigPath, localConfigParentDir);
        }

        String configName = getConfigNameFromTmpDir(tmpConfigDir);
        String k8sConfigPath = String.format("%s/%s", localConfigParentDir, configName);

        return k8sConfigPath;
    }

    public static String downloadHdfsAndHiveConf(FilesystemManager filesystemManager, SparkK8sConfig sparkK8sConfig) {
        String confMd5Sum = sparkK8sConfig.getMd5sum();
        String remotePath = sparkK8sConfig.getConfHdfsPath();
        String confFileDirName = String.format("%s/%s/%s", USER_DIR, tmpHadoopFilePath, confMd5Sum);
        File dirFile = new File(confFileDirName);

        try {
            if (!dirFile.exists()) {
                Files.createParentDirs(dirFile);
            }
        } catch (IOException e) {
            throw new RdosDefineException(String.format("can not create dir '%s' on engine", dirFile.getParent()));
        }

        if (dirFile.exists()) {
            File[] files = dirFile.listFiles();
            if (files != null && files.length > 0) {
                return confFileDirName;
            }
        } else {
            if (!dirFile.mkdir()) {
                throw new RdosDefineException(String.format("can not create dir '%s' on engine", confFileDirName));
            }
        }

        boolean downloadFlag = filesystemManager.downloadDir(remotePath, confFileDirName);
        if (!downloadFlag) {
            throw new RuntimeException("----download file exception---");
        }
        return confFileDirName;
    }

    public static String getConfigNameFromTmpDir(File tmpConfigDir) {
        String[] contentFiles = tmpConfigDir.list();
        if (contentFiles.length <= 1) {
            throw new RuntimeException("k8s config file not exist");
        }

        for (String fileName : contentFiles) {
            if (!fileName.endsWith(".zip")) {
                return fileName;
            }
        }
        return null;
    }

    public static io.fabric8.kubernetes.client.Config getK8sConfig(String kubeConfigFile) {
        final io.fabric8.kubernetes.client.Config config;
        if (kubeConfigFile != null) {
            LOG.debug("Trying to load kubernetes config from file: {}.", kubeConfigFile);
            try {
                config = io.fabric8.kubernetes.client.Config.fromKubeconfig(SparkConfigUtil.getContentFromFile(kubeConfigFile));
            } catch (IOException e) {
                throw new KubernetesClientException("Load kubernetes config failed.", e);
            }
        } else {
            LOG.debug("Trying to load default kubernetes config.");
            // Null means load from default context
            config = io.fabric8.kubernetes.client.Config.autoConfigure(null);
        }
        return config;
    }

    public static String getContentFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists()) {
            StringBuilder content = new StringBuilder();
            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error read file content.", e);
            }
            return content.toString();
        }
        throw new FileNotFoundException("File " + filePath + " not exists.");
    }


    public static void setHadoopUserName(SparkK8sConfig sparkK8sConfig, SparkConf sparkConf) {
        String hadoopUserName = sparkK8sConfig.getHadoopUserName();
        if (Strings.isNullOrEmpty(hadoopUserName)) {
            hadoopUserName = System.getenv(ExtendConfig.HADOOP_USER_NAME_KEY());
            if (StringUtils.isBlank(hadoopUserName)) {
                hadoopUserName = System.getProperty(ExtendConfig.HADOOP_USER_NAME_KEY());
            }
        }

        // driver use
        sparkConf.set(ExtendConfig.HADOOP_USER_NAME_KEY(), hadoopUserName);
        // executor use
        sparkConf.set(EXECUTOR_HADOOP_USER_NAME, hadoopUserName);
    }


}
