package com.dtstack.engine.dtscript.client;


import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.base.util.YarnClientUtils;
import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.am.ApplicationMaster;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import com.dtstack.engine.dtscript.common.exceptions.RequestOverLimitException;
import com.dtstack.engine.dtscript.util.Utilities;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.*;

public class Client {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    private static final String HDFS_SUPER_GROUP = "dfs.permissions.superusergroup";

    private DtYarnConfiguration conf;
    private BaseConfig baseConfig;
    private FileSystem dfs;
    private volatile YarnClient yarnClient;
    private volatile Path appJarSrc;


    private static FsPermission JOB_FILE_PERMISSION = FsPermission.createImmutable((short) 0644);

    public Client(DtYarnConfiguration conf, BaseConfig allConfig) throws Exception {
        this.conf = conf;
        this.baseConfig = allConfig;
        KerberosUtils.login(allConfig, () -> {
            String appSubmitterUserName = System.getenv(ApplicationConstants.Environment.USER.name());
            if (conf.get("hadoop.job.ugi") == null) {
                UserGroupInformation ugi = UserGroupInformation.createRemoteUser(appSubmitterUserName);
                conf.set("hadoop.job.ugi", ugi.getUserName() + "," + ugi.getUserName());
            }
            String proxyUser = conf.get(DtYarnConstants.PROXY_USER_NAME);
            String superGroup = conf.get(HDFS_SUPER_GROUP);
            if(StringUtils.isNotBlank(superGroup)){
                if (StringUtils.isNotBlank(proxyUser)) {
                    UserGroupInformation hadoopUserNameUGI = UserGroupInformation.createRemoteUser(superGroup);
                    UserGroupInformation.setLoginUser(UserGroupInformation.createProxyUser(proxyUser, hadoopUserNameUGI));
                } else {
                    UserGroupInformation.setLoginUser(UserGroupInformation.createRemoteUser(superGroup));
                }
            }

            this.yarnClient = getYarnClient();
            Path appJarSrc = new Path(JobConf.findContainingJar(ApplicationMaster.class));
            this.appJarSrc = appJarSrc;


            return null;
        }, conf);
    }

    public YarnConfiguration init(ClientArguments clientArguments) throws IOException, YarnException, ParseException, ClassNotFoundException {

        YarnConfiguration conf = new YarnConfiguration((YarnConfiguration) this.conf);
        String appSubmitterUserName = System.getenv(ApplicationConstants.Environment.USER.name());
        LOG.info("Got appSubmitterUserName: " + appSubmitterUserName);
        if (conf.get("hadoop.job.ugi") == null) {
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(appSubmitterUserName);
            conf.set("hadoop.job.ugi", ugi.getUserName() + "," + ugi.getUserName());
        }
        LOG.info("Got hadoop.job.ugi: " + conf.get("hadoop.job.ugi"));

        conf.set("ipc.client.fallback-to-simple-auth-allowed", "true");

        if (clientArguments.nodes != null) {
            conf.set(DtYarnConfiguration.CONTAINER_REQUEST_NODES, clientArguments.nodes);
        }
        conf.set(DtYarnConfiguration.DTSCRIPT_AM_MEMORY, String.valueOf(clientArguments.amMem));
        conf.set(DtYarnConfiguration.DTSCRIPT_AM_CORES, String.valueOf(clientArguments.amCores));
        conf.set(DtYarnConfiguration.DTSCRIPT_WORKER_MEMORY, String.valueOf(clientArguments.workerMemory));
        conf.set(DtYarnConfiguration.DTSCRIPT_WORKER_VCORES, String.valueOf(clientArguments.workerVcores));
        conf.set(DtYarnConfiguration.DTSCRIPT_WORKER_GPU, String.valueOf(clientArguments.workerGCores));
        conf.set(DtYarnConfiguration.DT_WORKER_NUM, String.valueOf(clientArguments.workerNum));
        conf.set(DtYarnConfiguration.APP_PRIORITY, String.valueOf(clientArguments.priority));
        conf.setBoolean(DtYarnConfiguration.DTSCRIPT_USER_CLASSPATH_FIRST, clientArguments.userClasspathFirst);

        int appAttempts = clientArguments.maxAppAttempts;
        if (appAttempts > conf.getInt(YarnConfiguration.RM_AM_MAX_ATTEMPTS, YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS)) {
            appAttempts = conf.getInt(YarnConfiguration.RM_AM_MAX_ATTEMPTS, YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS);
        }
        conf.set(DtYarnConfiguration.APP_MAX_ATTEMPTS, String.valueOf(appAttempts));

        conf.setBoolean(DtYarnConfiguration.APP_NODEMANAGER_EXCLUSIVE, clientArguments.exclusive);

        if (StringUtils.isNotBlank(clientArguments.nodeLabel)) {
            conf.set(DtYarnConfiguration.NODE_LABEL, String.valueOf(clientArguments.nodeLabel));
        }

        if (clientArguments.confs != null) {
            Enumeration<String> confSet = (Enumeration<String>) clientArguments.confs.propertyNames();
            while (confSet.hasMoreElements()) {
                String confArg = confSet.nextElement();
                conf.set(confArg, clientArguments.confs.getProperty(confArg));
            }
        }

        return conf;
    }

    public String submit(String[] args) throws IOException, YarnException, ParseException, ClassNotFoundException {
        ClientArguments clientArguments = new ClientArguments(args);

        YarnConfiguration conf = init(clientArguments);

        YarnClientApplication newAPP = getYarnClient().createApplication();
        GetNewApplicationResponse newAppResponse = newAPP.getNewApplicationResponse();
        ApplicationId applicationId = newAppResponse.getApplicationId();
        clientArguments.setApplicationId(applicationId.toString());
        LOG.info("Got new Application: " + applicationId.toString());

        Map<String, String> appMasterEnv = new HashMap<>();

        /** launch command */
        LOG.info("Building app launch command");
        String launchCmd = new LaunchCommandBuilder(clientArguments, conf).buildCmd();
        if (StringUtils.isNotBlank(launchCmd)) {
            appMasterEnv.put(DtYarnConstants.Environment.DT_EXEC_CMD.toString(), launchCmd);
        } else {
            throw new IllegalArgumentException("Invalid launch cmd for the application");
        }
        LOG.info("app launch command: " + launchCmd);

        Path jobConfPath = Utilities.getRemotePath(conf, applicationId, DtYarnConstants.LEARNING_JOB_CONFIGURATION);
        LOG.info("job conf path: " + jobConfPath);
        FSDataOutputStream out = FileSystem.create(jobConfPath.getFileSystem(conf), jobConfPath,
                new FsPermission(JOB_FILE_PERMISSION));
        conf.writeXml(out);
        out.close();

        Map<String, LocalResource> localResources = new HashMap<>();
        localResources.put(DtYarnConstants.LEARNING_JOB_CONFIGURATION,
                Utilities.createApplicationResource(getFileSystem(), jobConfPath, LocalResourceType.FILE));


        Path appMasterJar = Utilities.getRemotePath(conf, applicationId, DtYarnConfiguration.DTSCRIPT_APPMASTERJAR_PATH);
        LOG.info("Copying " + appJarSrc + " to remote path " + appMasterJar.toString());
        getFileSystem().copyFromLocalFile(false, true, appJarSrc, appMasterJar);
        localResources.put(DtYarnConfiguration.DTSCRIPT_APPMASTERJAR_PATH,
                Utilities.createApplicationResource(getFileSystem(), appMasterJar, LocalResourceType.FILE));


        StringBuilder classPathEnv = new StringBuilder("${CLASSPATH}:./*");

        for (String cp : conf.getStrings(DtYarnConfiguration.YARN_APPLICATION_CLASSPATH,
                DtYarnConfiguration.DEFAULT_DTSCRIPT_APPLICATION_CLASSPATH)) {
            classPathEnv.append(':');
            classPathEnv.append(cp.trim());
        }

        if (Boolean.FALSE.equals(clientArguments.localFile) && clientArguments.files != null) {
            StringBuffer appFilesRemotePath = new StringBuffer(1000);
            Path[] xlearningFilesDst = new Path[clientArguments.files.length];
            LOG.info("Copy dtscript files from local filesystem to remote.");
            for (int i = 0; i < clientArguments.files.length; i++) {
                assert (!clientArguments.files[i].isEmpty());

                if (!clientArguments.files[i].startsWith("hdfs:")) { //local
                    Path xlearningFilesSrc = new Path(clientArguments.files[i]);
                    xlearningFilesDst[i] = Utilities.getRemotePath(
                            conf, applicationId, new Path(clientArguments.files[i]).getName());
                    LOG.info("Copying " + clientArguments.files[i] + " to remote path " + xlearningFilesDst[i].toString());
                    getFileSystem().copyFromLocalFile(false, true, xlearningFilesSrc, xlearningFilesDst[i]);
                    appFilesRemotePath.append(xlearningFilesDst[i].toUri().toString()).append(",");
                } else { //hdfs
                    appFilesRemotePath.append(clientArguments.files[i]).append(",");
                }

            }
            appMasterEnv.put(DtYarnConstants.Environment.FILES_LOCATION.toString(),
                    appFilesRemotePath.deleteCharAt(appFilesRemotePath.length() - 1).toString());
        }

        if (StringUtils.isNotBlank(clientArguments.cacheFiles)) {
            String[] cacheFiles = StringUtils.split(clientArguments.cacheFiles, ",");
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

                if (!pathRemote.getFileSystem(conf).exists(pathRemote)) {
                    throw new IOException("cacheFile path " + pathRemote + " not existed!");
                }
            }
            appMasterEnv.put(DtYarnConstants.Environment.CACHE_FILE_LOCATION.toString(), clientArguments.cacheFiles);
        }

        appMasterEnv.put("CLASSPATH", classPathEnv.toString());
        appMasterEnv.put("HADOOP_HOME", conf.get(DtYarnConfiguration.DT_HADOOP_HOME_DIR));
        appMasterEnv.put(DtYarnConstants.Environment.OUTPUTS.toString(), clientArguments.outputs.toString());
        appMasterEnv.put(DtYarnConstants.Environment.INPUTS.toString(), clientArguments.inputs.toString());
        appMasterEnv.put(DtYarnConstants.Environment.APP_TYPE.toString(), clientArguments.appType.name());
        appMasterEnv.put(DtYarnConstants.Environment.XLEARNING_STAGING_LOCATION.toString(), Utilities.getRemotePath(conf, applicationId, "").toString());
        appMasterEnv.put(DtYarnConstants.Environment.APP_JAR_LOCATION.toString(), appMasterJar.toUri().toString());
        appMasterEnv.put(DtYarnConstants.Environment.XLEARNING_JOB_CONF_LOCATION.toString(), jobConfPath.toString());
        appMasterEnv.put(DtYarnConstants.Environment.XLEARNING_CONTAINER_MAX_MEMORY.toString(), String.valueOf(newAppResponse.getMaximumResourceCapability().getMemory()));


        LOG.info("Building application master launch command");
        List<String> appMasterArgs = new ArrayList<>(20);
        appMasterArgs.add(conf.get(DtYarnConfiguration.JAVA_PATH,"${JAVA_HOME}" + "/bin/java"));
        appMasterArgs.add("-cp " + "${CLASSPATH}");
        appMasterArgs.add("-Xms" + conf.getInt(DtYarnConfiguration.DTSCRIPT_AM_MEMORY, DtYarnConfiguration.DEFAULT_DTSCRIPT_AM_MEMORY) + "m");
        appMasterArgs.add("-Xmx" + conf.getInt(DtYarnConfiguration.DTSCRIPT_AM_MEMORY, DtYarnConfiguration.DEFAULT_DTSCRIPT_AM_MEMORY) + "m");
        String javaOpts = conf.get(DtYarnConfiguration.DTSCRIPT_APPMASTER_EXTRA_JAVA_OPTS, DtYarnConfiguration.DEFAULT_DTSCRIPT_APPMASTER_EXTRA_JAVA_OPTS);
        if (!StringUtils.isBlank(javaOpts)) {
            appMasterArgs.add(javaOpts);
        }

        appMasterArgs.add(ApplicationMaster.class.getName());
        appMasterArgs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR
                + "/" + ApplicationConstants.STDOUT);
        appMasterArgs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR
                + "/" + ApplicationConstants.STDERR);

        StringBuilder command = new StringBuilder();
        for (String arg : appMasterArgs) {
            command.append(arg).append(" ");
        }

        LOG.info("Application master launch command: " + command.toString());
        List<String> appMasterLaunchcommands = new ArrayList<>();
        appMasterLaunchcommands.add(command.toString());

        ApplicationSubmissionContext applicationContext = newAPP.getApplicationSubmissionContext();
        applicationContext.setApplicationId(applicationId);
        applicationContext.setApplicationName(clientArguments.appName);
        applicationContext.setApplicationType(clientArguments.appType.name());
        applicationContext.setMaxAppAttempts(clientArguments.maxAppAttempts);
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemory(conf.getInt(DtYarnConfiguration.DTSCRIPT_AM_MEMORY, DtYarnConfiguration.DEFAULT_DTSCRIPT_AM_MEMORY));
        capability.setVirtualCores(conf.getInt(DtYarnConfiguration.DTSCRIPT_AM_CORES, DtYarnConfiguration.DEFAULT_DTSCRIPT_AM_CORES));
        applicationContext.setResource(capability);
        ByteBuffer tokenBuffer = SecurityUtil.getDelegationTokens(conf, getYarnClient());
        ContainerLaunchContext amContainer = ContainerLaunchContext.newInstance(
                localResources, appMasterEnv, appMasterLaunchcommands, null, tokenBuffer, null);


        applicationContext.setAMContainerSpec(amContainer);

        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(conf.getInt(DtYarnConfiguration.APP_PRIORITY, DtYarnConfiguration.DEFAULT_DTSCRIPT_APP_PRIORITY));
        applicationContext.setPriority(priority);
        applicationContext.setQueue(conf.get(DtYarnConfiguration.DT_APP_QUEUE, DtYarnConfiguration.DEFAULT_DT_APP_QUEUE));
        String nodeLabels = conf.get(DtYarnConfiguration.NODE_LABEL);
        if (StringUtils.isNotBlank(nodeLabels)) {
            applicationContext.setNodeLabelExpression(nodeLabels);
        }
        applicationId = getYarnClient().submitApplication(applicationContext);

        return applicationId.toString();
    }


    private void checkArguments(DtYarnConfiguration conf, GetNewApplicationResponse newApplication) {
        int maxMem = newApplication.getMaximumResourceCapability().getMemory();
        LOG.info("Max mem capability of resources in this cluster " + maxMem);
        int maxVCores = newApplication.getMaximumResourceCapability().getVirtualCores();
        LOG.info("Max vcores capability of resources in this cluster " + maxVCores);

        int amMem = conf.getInt(DtYarnConfiguration.DTSCRIPT_AM_MEMORY, DtYarnConfiguration.DEFAULT_DTSCRIPT_AM_MEMORY);
        int amCores = conf.getInt(DtYarnConfiguration.DTSCRIPT_AM_CORES, DtYarnConfiguration.DEFAULT_DTSCRIPT_AM_CORES);
        if (amMem > maxMem) {
            throw new RequestOverLimitException("AM memory requested " + amMem +
                    " above the max threshold of yarn cluster " + maxMem);
        }
        if (amMem <= 0) {
            throw new IllegalArgumentException(
                    "Invalid memory specified for application master, exiting."
                            + " Specified memory=" + amMem);
        }
        LOG.info("Apply for am Memory " + amMem + "M");
        if (amCores > maxVCores) {
            throw new RequestOverLimitException("am vcores requested " + amCores +
                    " above the max threshold of yarn cluster " + maxVCores);
        }
        if (amCores <= 0) {
            throw new IllegalArgumentException(
                    "Invalid vcores specified for am, exiting."
                            + "Specified vcores=" + amCores);
        }
        LOG.info("Apply for am vcores " + amCores);

        int workerNum = conf.getInt(DtYarnConfiguration.DT_WORKER_NUM, DtYarnConfiguration.DEFAULT_DT_WORKER_NUM);
        int workerMemory = conf.getInt(DtYarnConfiguration.DTSCRIPT_WORKER_MEMORY, DtYarnConfiguration.DEFAULT_DTSCRIPT_WORKER_MEMORY);
        int workerVcores = conf.getInt(DtYarnConfiguration.DTSCRIPT_WORKER_VCORES, DtYarnConfiguration.DEFAULT_DTSCRIPT_WORKER_VCORES);
        if (workerNum < 1) {
            throw new IllegalArgumentException(
                    "Invalid no. of worker specified, exiting."
                            + " Specified container number=" + workerNum);
        }
        LOG.info("Apply for worker number " + workerNum);
        if (workerMemory > maxMem) {
            throw new RequestOverLimitException("Worker memory requested " + workerMemory +
                    " above the max threshold of yarn cluster " + maxMem);
        }
        if (workerMemory <= 0) {
            throw new IllegalArgumentException(
                    "Invalid memory specified for worker, exiting."
                            + "Specified memory=" + workerMemory);
        }
        LOG.info("Apply for worker Memory " + workerMemory + "M");
        if (workerVcores > maxVCores) {
            throw new RequestOverLimitException("Worker vcores requested " + workerVcores +
                    " above the max threshold of yarn cluster " + maxVCores);
        }
        if (workerVcores <= 0) {
            throw new IllegalArgumentException(
                    "Invalid vcores specified for worker, exiting."
                            + "Specified vcores=" + workerVcores);
        }
        LOG.info("Apply for worker vcores " + workerVcores);

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
        yarnClient = YarnClientUtils.getYarnClient(yarnClient, baseConfig, conf);
        return yarnClient;
    }

    public FileSystem getFileSystem() throws IOException {
        try {
            if (dfs == null) {
                synchronized (this) {
                    dfs = FileSystem.get(conf);
                }
            } else {
                dfs.getStatus();
            }
        } catch (Throwable e) {
            LOG.error("getFileSystem error:{}", e);
            synchronized (this) {
                if (dfs != null) {
                    boolean flag = true;
                    try {
                        //判断下是否可用
                        dfs.getStatus();
                    } catch (Throwable e1) {
                        LOG.error("getFileSystem error:{}", e1);
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
                    dfs = FileSystem.get(conf);
                }
            }
        }
        return dfs;
    }

    public List<String> getContainerInfos(String jobId) throws IOException {
        Path remotePath = new Path(conf.get(DtYarnConfiguration.CONTAINER_STAGING_DIR, DtYarnConfiguration.DEFAULT_CONTAINER_STAGING_DIR), jobId);
        FileStatus[] status = getFileSystem().listStatus(remotePath);
        List<String> infos = new ArrayList<>(status.length);
        for (FileStatus file : status) {
            if (!file.getPath().getName().startsWith("container")) {
                continue;
            }
            FSDataInputStream inputStream = getFileSystem().open(file.getPath());
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder lineString = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                lineString.append(line);
            }
            infos.add(lineString.toString());
        }
        return infos;
    }
}