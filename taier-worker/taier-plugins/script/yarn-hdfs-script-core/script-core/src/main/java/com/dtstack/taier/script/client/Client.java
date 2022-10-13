package com.dtstack.taier.script.client;


import com.dtstack.taier.base.BaseConfig;
import com.dtstack.taier.base.exception.EnginePluginsBaseException;
import com.dtstack.taier.base.util.HadoopUtils;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.dtstack.taier.script.ScriptConfiguration;
import com.dtstack.taier.script.am.ApplicationMaster;
import com.dtstack.taier.script.api.ScriptConstants;
import com.dtstack.taier.script.common.SecurityUtil;
import com.dtstack.taier.script.common.type.AbstractAppType;
import com.dtstack.taier.script.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private YarnConfiguration yarnconf;
    private FileSystem dfs;
    private volatile YarnClient yarnClient;
    private volatile Path appJarSrc;
    private YarnFileUploader fileUploader;
    private ThreadPoolExecutor threadPoolExecutor;
    private volatile BaseConfig baseConfig;

    private static FsPermission JOB_FILE_PERMISSION = FsPermission.createImmutable((short) 0644);

    public Client(YarnConfiguration yarnConf, ScriptConfiguration dtConf, BaseConfig allConfig) throws Exception {
        this.yarnconf = yarnConf;
        this.baseConfig = allConfig;
        this.threadPoolExecutor = new ThreadPoolExecutor(
                yarnConf.getInt(ScriptConfiguration.SCRIPT_ASYNC_CHECK_YARN_CLIENT_THREAD_NUM, ScriptConfiguration.DEFAULT_SCRIPT_ASYNC_CHECK_YARN_CLIENT_THREAD_NUM),
                yarnConf.getInt(ScriptConfiguration.SCRIPT_ASYNC_CHECK_YARN_CLIENT_THREAD_NUM, ScriptConfiguration.DEFAULT_SCRIPT_ASYNC_CHECK_YARN_CLIENT_THREAD_NUM),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1000), new CustomThreadFactory("script_yarnclient"));
        KerberosUtils.login(allConfig, () -> {
            this.yarnClient = getYarnClient();
            Path appJarSrc = new Path(JobConf.findContainingJar(ApplicationMaster.class));
            this.appJarSrc = appJarSrc;
            return null;
        }, yarnConf);
    }

    public String submit(ScriptConfiguration dtconf) throws Exception {
        ApplicationId applicationId = null;
        try {
            YarnClientApplication newAPP = getYarnClient().createApplication();
            GetNewApplicationResponse newAppResponse = newAPP.getNewApplicationResponse();
            applicationId = newAppResponse.getApplicationId();
            dtconf.set(ScriptConfiguration.SCRIPT_RUNNING_APPLICATIONID, applicationId.toString());

            Boolean localFile = false;
            yarnconf.set("ipc.client.fallback-to-simple-auth-allowed", "true");
            String[] files = StringUtils.split(dtconf.get(ScriptConfiguration.SCRIPT_FILES), ",");
            // judge file is remote or local
            if (files != null && !files[0].startsWith("hdfs:") && !files[0].startsWith("http:")) {
                localFile = Boolean.TRUE;
            }
            dtconf.setBoolean(ScriptConfiguration.SCRIPT_LOCALFILE, localFile);

            LOG.info("Got new Application: {}", applicationId);
            Map<String, String> appMasterEnv = new HashMap<>();

            /** launch command */
            LOG.info("Building app launch command");
            // using 「app.type」to determine what task should we run
            String launchCmd = AbstractAppType.fromString(dtconf.get("script.app.type")).buildCmd(dtconf);
            if (StringUtils.isNotBlank(launchCmd)) {
                appMasterEnv.put(ScriptConstants.Environment.EXEC_CMD.toString(), launchCmd);
            } else {
                throw new IllegalArgumentException("Invalid launch cmd for the application");
            }

            LOG.info("app launch command:{}", launchCmd);
            String appEnv = dtconf.get(ScriptConfiguration.SCRIPT_APP_ENV, null);
            if (StringUtils.isNotBlank(appEnv)) {
                LOG.info("app env : {}", appEnv);
                appMasterEnv.put(ScriptConstants.Environment.APP_ENV.toString(), appEnv);
            }

            Map<String, LocalResource> localResources = new HashMap<>();
            // add shipFilename
            String shipFileName = "";
            if (StringUtils.isNoneBlank(dtconf.get(ScriptConfiguration.SCRIPT_SHIP_FILES))) {
                String shipFiles = dtconf.get(ScriptConfiguration.SCRIPT_SHIP_FILES);
                String[] shipFile = shipFiles.split(",");
                for (int i = 0; i < shipFile.length; i++) {
                    if (shipFile[i].startsWith("hdfs://")) {
                        boolean exists = getFileSystem().exists(new Path(shipFile[i]));
                        if (!exists) {
                            LOG.error("{} not exists", shipFile[i]);
                            throw new EnginePluginsBaseException(shipFile[i] + " not exists");
                        }
                    } else {
                        Path localShipFilePath = new Path(shipFile[i]);
                        String fileName = localShipFilePath.getName();
                        Path remoteShipFilePath = Utilities.getRemotePath(yarnconf, dtconf, applicationId, fileName);
                        getFileSystem().copyFromLocalFile(false, true, localShipFilePath, remoteShipFilePath);
                        localResources.put(fileName,
                                Utilities.createApplicationResource(getFileSystem(), remoteShipFilePath, LocalResourceType.FILE));
                        shipFile[i] = remoteShipFilePath.toString();
                    }
                }
                shipFileName = StringUtils.join(shipFile, ",");
            }

            // add log4j.properties
            Path remoteLog4j = Utilities.getRemotePath(yarnconf, dtconf, applicationId, ScriptConfiguration.SCRIPT_LOG4J_FILENAME);
            File log4jFile = new File(new File(appJarSrc.toString()).getParent(), ScriptConfiguration.SCRIPT_LOG4J_FILENAME);
            if (!log4jFile.exists()) {
                File tmpFileDir = new File(System.getProperty("user.dir") + File.separator + "tmp");
                if (!tmpFileDir.exists()) {
                    tmpFileDir.mkdirs();
                }
                File tmpLog4jFile = File.createTempFile(applicationId + "-log4j.properties", null, tmpFileDir);
                try (FileWriter fwrt = new FileWriter(tmpLog4jFile)) {
                    String logLevel = StringUtils.upperCase(dtconf.get(ScriptConfiguration.SCRIPT_LOGLEVEL));
                    String log4jContent = StringUtils.replace(ScriptConfiguration.DEFAULT_LOG4J_CONTENT, "INFO", logLevel);
                    fwrt.write(log4jContent);
                    fwrt.flush();
                } catch (Exception e) {
                    LOG.error("Write log4j.properties error", e);
                    tmpLog4jFile.delete();
                    throw new EnginePluginsBaseException(e);
                }
                log4jFile = tmpLog4jFile;
            }
            boolean hasLog4j = false;
            ;
            try {
                Path localLog4j = new Path(log4jFile.toString());
                if (log4jFile.exists()) {
                    LOG.info("Copying " + localLog4j + " to remote path " + remoteLog4j);
                    getFileSystem().copyFromLocalFile(false, true, localLog4j, remoteLog4j);
                    localResources.put(ScriptConfiguration.SCRIPT_LOG4J_FILENAME,
                            Utilities.createApplicationResource(getFileSystem(), remoteLog4j, LocalResourceType.FILE));
                    hasLog4j = true;
                }
                dtconf.setBoolean(ScriptConfiguration.SCRIPT_HAS_LOG4J, hasLog4j);
            } finally {
                if (StringUtils.startsWith(log4jFile.getName(), applicationId.toString())) {
                    log4jFile.delete();
                }
            }

            // add core-site.xml
            Path yarnConfPath = Utilities.getRemotePath(yarnconf, dtconf, applicationId, ScriptConstants.YARN_CONFIGURATION);
            try (FSDataOutputStream coreSiteXml = FileSystem.create(yarnConfPath.getFileSystem(yarnconf), yarnConfPath,
                    new FsPermission(JOB_FILE_PERMISSION))) {
                yarnconf.writeXml(coreSiteXml);
            } catch (Exception e) {
                LOG.error("Write core-site.xml error", e);
                throw new EnginePluginsBaseException(e);
            }
            localResources.put(ScriptConstants.YARN_CONFIGURATION,
                    Utilities.createApplicationResource(getFileSystem(), yarnConfPath, LocalResourceType.FILE));

            // add script-site.xml to container
            Path scriptDtConfPath = Utilities.getRemotePath(yarnconf, dtconf, applicationId, ScriptConstants.SCRIPT_CONFIGURATION);
            LOG.info("job conf path: {}", scriptDtConfPath);
            try (FSDataOutputStream scriptSiteXml = FileSystem.create(scriptDtConfPath.getFileSystem(yarnconf), scriptDtConfPath,
                    new FsPermission(JOB_FILE_PERMISSION))) {
                dtconf.writeXml(scriptSiteXml);
            } catch (Exception e) {
                LOG.error("Write script-site.xml error", e);
                throw new EnginePluginsBaseException(e);
            }
            localResources.put(ScriptConstants.SCRIPT_CONFIGURATION,
                    Utilities.createApplicationResource(getFileSystem(), scriptDtConfPath, LocalResourceType.FILE));

            // copy yarn2-hdfs2-script client to remote hdfs path:AppMaster.jar
            Path appMasterJar = Utilities.getRemotePath(yarnconf, dtconf, applicationId, ScriptConfiguration.SCRIPT_APPMASTERJAR_PATH);
            LOG.info("Copying {} to remote path {}", appJarSrc, appMasterJar);
            getFileSystem().copyFromLocalFile(false, true, appJarSrc, appMasterJar);
            localResources.put(ScriptConfiguration.SCRIPT_APPMASTERJAR_PATH,
                    Utilities.createApplicationResource(getFileSystem(), appMasterJar, LocalResourceType.FILE));

            // classPathEnv
            StringBuilder classPathEnv = new StringBuilder(appMasterJar.getName());
            for (String cp : yarnconf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH,
                    ScriptConfiguration.DEFAULT_SCRIPT_APPLICATION_CLASSPATH)) {
                classPathEnv.append(':');
                classPathEnv.append(cp.trim());
            }
            classPathEnv.append("./*");

            if (StringUtils.isNotBlank(dtconf.get(ScriptConfiguration.SCRIPT_CACHEFILES))) {
                String[] cacheFiles = StringUtils.split(dtconf.get(ScriptConfiguration.SCRIPT_CACHEFILES), ",");
                for (String path : cacheFiles) {
                    Path pathRemote;
                    if (path.contains("#")) {
                        String[] paths = StringUtils.split(path, "#");
                        if (paths.length != 2) {
                            throw new RuntimeException("Error cacheFile path format " + path);
                        }
                        pathRemote = new Path(paths[0]);
                    } else {
                        pathRemote = new Path(path);
                    }

                    if (!pathRemote.getFileSystem(yarnconf).exists(pathRemote)) {
                        throw new IOException("cacheFile path " + pathRemote + " not existed!");
                    }
                }
                appMasterEnv.put(ScriptConstants.Environment.CACHE_FILE_LOCATION.toString(), dtconf.get(ScriptConfiguration.SCRIPT_CACHEFILES));
            }

            appMasterEnv.put("CLASSPATH", classPathEnv.toString());
            if (StringUtils.isNoneBlank(dtconf.get(ScriptConfiguration.HADOOP_HOME_DIR))) {
                appMasterEnv.put("HADOOP_HOME", dtconf.get(ScriptConfiguration.HADOOP_HOME_DIR));
            }

            String hadoopUserName = dtconf.get("hadoop.username");
            if (StringUtils.isBlank(hadoopUserName)) {
                hadoopUserName = HadoopUtils.getHadoopUserName(baseConfig);
            }

            appMasterEnv.putAll(Utilities.getEnvironmentVariables(ScriptConstants.SCRIPT_ENV_PREFIX, yarnconf));
            appMasterEnv.put(ScriptConstants.Environment.SCRIPT_FILES.toString(), dtconf.get(ScriptConfiguration.SCRIPT_FILES));
            appMasterEnv.put(ScriptConstants.Environment.HADOOP_USER_NAME.toString(), hadoopUserName);
            appMasterEnv.put(ScriptConstants.Environment.SCRIPT_SHIP_FILES.toString(), shipFileName);
            appMasterEnv.put(ScriptConstants.Environment.OUTPUTS.toString(), dtconf.get(ScriptConfiguration.SCRIPT_OUTPUTS, ""));
            appMasterEnv.put(ScriptConstants.Environment.INPUTS.toString(), dtconf.get(ScriptConfiguration.SCRIPT_INPUTS, ""));
            appMasterEnv.put(ScriptConstants.Environment.APP_TYPE.toString(), dtconf.get(ScriptConfiguration.SCRIPT_APP_TYPE));
            appMasterEnv.put(ScriptConstants.Environment.SCRIPT_STAGING_LOCATION.toString(), Utilities.getRemotePath(yarnconf, dtconf, applicationId, "").toString());
            appMasterEnv.put(ScriptConstants.Environment.APP_JAR_LOCATION.toString(), appMasterJar.toUri().toString());
            appMasterEnv.put(ScriptConstants.Environment.YARN_JOB_CONF_LOCATION.toString(), yarnConfPath.toString());
            appMasterEnv.put(ScriptConstants.Environment.SCRIPT_JOB_CONF_LOCATION.toString(), scriptDtConfPath.toString());
            appMasterEnv.put(ScriptConstants.Environment.LOG4J_JOB_CONF_LOCATION.toString(), remoteLog4j.toString());
            appMasterEnv.put(ScriptConstants.Environment.SCRIPT_CONTAINER_MAX_MEMORY.toString(), String.valueOf(newAppResponse.getMaximumResourceCapability().getMemory()));

            LOG.info("Building application master launch command");
            List<String> appMasterArgs = new ArrayList<>(20);
            appMasterArgs.add(dtconf.get(ScriptConfiguration.JAVA_PATH, "${JAVA_HOME}" + "/bin/java"));
            appMasterArgs.add("-cp " + "${CLASSPATH}");
            appMasterArgs.add("-Xms" + dtconf.getInt(ScriptConfiguration.SCRIPT_AM_MEMORY, ScriptConfiguration.DEFAULT_SCRIPT_AM_MEMORY) + "m");
            appMasterArgs.add("-Xmx" + dtconf.getInt(ScriptConfiguration.SCRIPT_AM_MEMORY, ScriptConfiguration.DEFAULT_SCRIPT_AM_MEMORY) + "m");
            if (hasLog4j) {
                appMasterArgs.add("-Dlog.file=" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/master.log");
                appMasterArgs.add("-Dlog4j.configuration=file:" + ScriptConfiguration.SCRIPT_LOG4J_FILENAME);
            }

            if (null != baseConfig && baseConfig.isOpenKerberos()) {
                appMasterArgs.add("-Djava.security.krb5.conf=" + ScriptConstants.LOCALIZED_KR5B_PATH);
            }

            String javaOpts = dtconf.get(ScriptConfiguration.SCRIPT_JAVA_OPTS, ScriptConfiguration.DEFAULT_SCRIPT_APPMASTER_EXTRA_JAVA_OPTS);
            if (!StringUtils.isBlank(javaOpts)) {
                appMasterArgs.add(javaOpts);
            }
            appMasterArgs.add(ApplicationMaster.class.getName());
            appMasterArgs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR
                    + "/" + "master.out");
            appMasterArgs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR
                    + "/" + "master.err");
            StringBuilder command = new StringBuilder();
            for (String arg : appMasterArgs) {
                command.append(arg).append(" ");
            }
            LOG.info("Application master launch command: {}", command);
            List<String> appMasterLaunchCommands = new ArrayList<>();
            appMasterLaunchCommands.add(command.toString());

            ApplicationSubmissionContext applicationContext = newAPP.getApplicationSubmissionContext();
            applicationContext.setApplicationId(applicationId);
            applicationContext.setApplicationName(dtconf.get(ScriptConfiguration.SCRIPT_APP_NAME));
            applicationContext.setApplicationType(dtconf.get(ScriptConfiguration.SCRIPT_APP_TYPE));
            int appAttempts = Integer.parseInt(dtconf.get(ScriptConfiguration.APP_MAX_ATTEMPTS, String.valueOf(ScriptConfiguration.DEFAULT_APP_MAX_ATTEMPTS)));
            if (appAttempts > yarnconf.getInt(YarnConfiguration.RM_AM_MAX_ATTEMPTS, YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS)) {
                appAttempts = yarnconf.getInt(YarnConfiguration.RM_AM_MAX_ATTEMPTS, YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS);
            }
            dtconf.set(ScriptConfiguration.APP_MAX_ATTEMPTS, String.valueOf(appAttempts));
            applicationContext.setMaxAppAttempts(appAttempts);
            Resource capability = Records.newRecord(Resource.class);
            capability.setMemory(dtconf.getInt(ScriptConfiguration.SCRIPT_AM_MEMORY, ScriptConfiguration.DEFAULT_SCRIPT_AM_MEMORY));
            capability.setVirtualCores(dtconf.getInt(ScriptConfiguration.SCRIPT_AM_CORES, ScriptConfiguration.DEFAULT_SCRIPT_AM_CORES));
            applicationContext.setResource(capability);

            ByteBuffer tokenBuffer = null;
            fileUploader = new YarnFileUploader(yarnconf, dtconf, applicationId);
            if (null != baseConfig && baseConfig.isOpenKerberos()) {
                tokenBuffer = SecurityUtil.getDelegationTokens(yarnconf, getYarnClient());
                setKrbResource(yarnconf, applicationId, appMasterEnv, dtconf.get(ScriptConfiguration.SCRIPT_APP_TYPE));
                Path remoteKrb5Conf = Utilities.getRemotePath(yarnconf, dtconf, applicationId, ScriptConstants.LOCALIZED_KR5B_PATH);
                localResources.put(ScriptConstants.LOCALIZED_KR5B_PATH,
                        Utilities.createApplicationResource(getFileSystem(), remoteKrb5Conf, LocalResourceType.FILE));
            }

            ContainerLaunchContext amContainer = ContainerLaunchContext.newInstance(
                    localResources, appMasterEnv, appMasterLaunchCommands, null, tokenBuffer, null);
            applicationContext.setAMContainerSpec(amContainer);

            Priority priority = Records.newRecord(Priority.class);
            priority.setPriority(dtconf.getInt(ScriptConfiguration.APP_PRIORITY, ScriptConfiguration.DEFAULT_SCRIPT_APP_PRIORITY));
            applicationContext.setPriority(priority);
            applicationContext.setQueue(dtconf.get(ScriptConfiguration.APP_QUEUE, ScriptConfiguration.DEFAULT_APP_QUEUE));
            String nodeLabels = dtconf.get(ScriptConfiguration.NODE_LABEL);
            if (StringUtils.isNotBlank(nodeLabels)) {
                applicationContext.setNodeLabelExpression(nodeLabels);
            }
            applicationId = getYarnClient().submitApplication(applicationContext);
            return applicationId.toString();
        } catch (Exception e) {
            Utilities.cleanStagingRemotePath(yarnconf, dtconf, applicationId);
            throw e;
        }
    }

    /**
     * 上传keytab、krb5.conf、py4j-gateway-server jar文件，供给Worker容器使用。
     *
     * @param yarnConf
     * @param applicationId
     * @param appMasterEnv
     * @throws IOException
     */
    private void setKrbResource(YarnConfiguration yarnConf, ApplicationId applicationId, Map<String, String> appMasterEnv, String appType) throws IOException {
        String[] krbPaths = KerberosUtils.getKerberosFile(baseConfig, null);
        Path localKeytabPath = new Path(krbPaths[0]);
        Path localKrb5Path = new Path(krbPaths[1]);

        fileUploader.uploadSingleResource(localKeytabPath, ScriptConstants.LOCALIZED_KEYTAB_PATH);
        fileUploader.uploadSingleResource(localKrb5Path, ScriptConstants.LOCALIZED_KR5B_PATH);
        appMasterEnv.put(ScriptConstants.ENV_PRINCIPAL, baseConfig.getPrincipal());
    }

    class YarnFileUploader {

        private YarnConfiguration yarnconf;
        private ApplicationId applicationId;
        private ScriptConfiguration dtconf;

        public YarnFileUploader(YarnConfiguration conf, ScriptConfiguration dtconf, ApplicationId applicationId) {
            this.yarnconf = conf;
            this.applicationId = applicationId;
            this.dtconf = dtconf;
        }

        public void uploadSingleResource(Path localPath, String remoteFileName) throws IOException {
            Path remotePath = Utilities.getRemotePath(this.yarnconf, this.dtconf, this.applicationId, remoteFileName);
            uploadLocalFileToRemote(localPath, remotePath);
        }

        private void uploadLocalFileToRemote(Path srcPath, Path dstPath) throws IOException {
            LOG.info("Copying {} to remote path {}", srcPath, dstPath);
            getFileSystem().copyFromLocalFile(false, true, srcPath, dstPath);
        }
    }

    public void kill(String jobId) throws IOException, YarnException {
        ApplicationId appId = ConverterUtils.toApplicationId(jobId);
        getYarnClient().killApplication(appId);
    }

    public ApplicationReport getApplicationReport(String jobId) throws IOException, YarnException {
        ApplicationId appId = ConverterUtils.toApplicationId(jobId);
        return getYarnClient().getApplicationReport(appId);
    }

    public YarnClient getYarnClient() {
        try {
            if (yarnClient == null) {
                synchronized (this) {
                    if (yarnClient == null) {
                        LOG.info("Build YarnClient!");
                        YarnClient realYarnClient = YarnClient.createYarnClient();
                        realYarnClient.init(yarnconf);
                        realYarnClient.start();
                        yarnClient = realYarnClient;
                    }
                }
            } else {
                //异步超时判断下是否可用，kerberos 开启下会出现hang死情况
                RetryUtil.asyncExecuteWithRetry(() -> yarnClient.getAllQueues(),
                        1,
                        0,
                        false,
                        30000L,
                        threadPoolExecutor);
            }
        } catch (Throwable e) {
            LOG.error("YarnClient UnHealth, ReBuild YarnClient", e);
            YarnClient yarnClient1 = YarnClient.createYarnClient();
            yarnClient1.init(yarnconf);
            yarnClient1.start();
            yarnClient = yarnClient1;
        }
        return yarnClient;
    }

    public FileSystem getFileSystem() throws IOException {
        try {
            if (dfs == null) {
                synchronized (this) {
                    dfs = FileSystem.get(yarnconf);
                }
            } else {
                dfs.getStatus();
            }
        } catch (Throwable e) {
            LOG.error("getFileSystem error:", e);
            synchronized (this) {
                if (dfs != null) {
                    boolean flag = true;
                    try {
                        //判断下是否可用
                        dfs.getStatus();
                    } catch (Throwable e1) {
                        LOG.error("getFileSystem error:", e1);
                        flag = false;
                    }
                    if (!flag) {
                        try {
                            dfs.close();
                        } finally {
                            dfs = null;
                        }
                    }
                }
                if (dfs == null) {
                    dfs = FileSystem.get(yarnconf);
                }
            }
        }
        return dfs;
    }
}