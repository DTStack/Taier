package com.dtstack.learning.client;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.learning.AM.ApplicationMaster;
import com.dtstack.learning.api.ApplicationMessageProtocol;
import com.dtstack.learning.api.LearningConstants;
import com.dtstack.learning.common.LogType;
import com.dtstack.learning.common.Message;
import com.dtstack.learning.common.exceptions.RequestOverLimitException;
import com.dtstack.learning.conf.LearningConfiguration;
import com.dtstack.learning.util.SecurityUtil;
import com.dtstack.learning.util.Utilities;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private static final Log LOG = LogFactory.getLog(Client.class);
    private LearningConfiguration conf;
    private volatile YarnClient yarnClient;
    private volatile Path appJarSrc;
    private FileSystem dfs;

    private ThreadPoolExecutor threadPoolExecutor;

    private volatile BaseConfig baseConfig;

    private static FsPermission JOB_FILE_PERMISSION = FsPermission.createImmutable((short) 0644);

    public Client(LearningConfiguration conf, BaseConfig allConfig) throws Exception {
        this.conf = conf;
        this.baseConfig = allConfig;
        this.threadPoolExecutor = new ThreadPoolExecutor(
                3,
                3,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("learning_yarnclient"));

        KerberosUtils.login(allConfig, () -> {
            this.yarnClient = getYarnClient();
            Path appJarSrc = new Path(JobConf.findContainingJar(ApplicationMaster.class));
            this.appJarSrc = appJarSrc;
            return null;
        }, conf);

    }

    private Client(String[] args) throws IOException, ParseException, ClassNotFoundException {
        this.conf = new LearningConfiguration();
//        this.dfs = FileSystem.get(conf);
        JOB_FILE_PERMISSION = FsPermission.createImmutable((short) 0644);
    }

    private YarnConfiguration init(ClientArguments clientArguments, Map<String, String> appMasterUserEnv, Map<String, String> containerUserEnv) throws IOException, YarnException{
        YarnConfiguration conf = new YarnConfiguration(this.conf);

        String appSubmitterUserName = System.getenv(ApplicationConstants.Environment.USER.name());
        if (conf.get("hadoop.job.ugi") == null) {
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(appSubmitterUserName);
            conf.set("hadoop.job.ugi", ugi.getUserName() + "," + ugi.getUserName());
        }

        conf.set(LearningConfiguration.XLEARNING_AM_MEMORY, String.valueOf(clientArguments.amMem));
        conf.set(LearningConfiguration.XLEARNING_AM_CORES, String.valueOf(clientArguments.amCores));
        conf.set(LearningConfiguration.XLEARNING_WORKER_MEMORY, String.valueOf(clientArguments.workerMemory));
        conf.set(LearningConfiguration.XLEARNING_WORKER_VCORES, String.valueOf(clientArguments.workerVCores));
        conf.set(LearningConfiguration.XLEARNING_WORKER_GPU, String.valueOf(clientArguments.workerVCores));
        conf.set(LearningConfiguration.XLEARNING_WORKER_NUM, String.valueOf(clientArguments.workerNum));
        conf.set(LearningConfiguration.XLEARNING_CHIEF_WORKER_MEMORY, String.valueOf(clientArguments.chiefWorkerMemory));
        conf.set(LearningConfiguration.XLEARNING_EVALUATOR_WORKER_MEMORY, String.valueOf(clientArguments.evaluatorWorkerMemory));
        conf.set(LearningConfiguration.XLEARNING_PS_MEMORY, String.valueOf(clientArguments.psMemory));
        conf.set(LearningConfiguration.XLEARNING_PS_VCORES, String.valueOf(clientArguments.psVCores));
        conf.set(LearningConfiguration.XLEARNING_PS_GPU, String.valueOf(clientArguments.psGCores));
        conf.set(LearningConfiguration.XLEARNING_PS_NUM, String.valueOf(clientArguments.psNum));
        conf.set(LearningConfiguration.XLEARNING_APP_PRIORITY, String.valueOf(clientArguments.priority));
        conf.setBoolean(LearningConfiguration.XLEARNING_USER_CLASSPATH_FIRST, clientArguments.userClasspathFirst);
        conf.set(LearningConfiguration.XLEARNING_TF_BOARD_WORKER_INDEX, String.valueOf(clientArguments.boardIndex));
        conf.set(LearningConfiguration.XLEARNING_TF_BOARD_RELOAD_INTERVAL, String.valueOf(clientArguments.boardReloadInterval));
        conf.set(LearningConfiguration.XLEARNING_TF_BOARD_ENABLE, String.valueOf(clientArguments.boardEnable));
        conf.set(LearningConfiguration.XLEARNING_TF_BOARD_LOG_DIR, clientArguments.boardLogDir);
        conf.set(LearningConfiguration.XLEARNING_TF_BOARD_HISTORY_DIR, clientArguments.boardHistoryDir);
        conf.set(LearningConfiguration.XLEARNING_BOARD_MODELPB, clientArguments.boardModelPB);
        conf.set(LearningConfiguration.XLEARNING_BOARD_CACHE_TIMEOUT, String.valueOf(clientArguments.boardCacheTimeout));
        conf.set(LearningConfiguration.XLEARNING_DOCKER_IMAGE, String.valueOf(clientArguments.dockerImage));
        conf.set(LearningConfiguration.XLEARNING_CONTAINER_TYPE, String.valueOf(clientArguments.containerType));
        conf.set(LearningConfiguration.XLEARNING_INPUT_STRATEGY, clientArguments.inputStrategy);
        conf.set(LearningConfiguration.XLEARNING_OUTPUT_STRATEGY, clientArguments.outputStrategy);
        conf.setBoolean(LearningConfiguration.XLEARNING_INPUTFILE_RENAME, clientArguments.isRenameInputFile);
        conf.setBoolean(LearningConfiguration.XLEARNING_INPUT_STREAM_SHUFFLE, clientArguments.inputStreamShuffle);
        conf.setClass(LearningConfiguration.XLEARNING_INPUTF0RMAT_CLASS, clientArguments.inputFormatClass, InputFormat.class);
        conf.setClass(LearningConfiguration.XLEARNING_OUTPUTFORMAT_CLASS, clientArguments.outputFormatClass, OutputFormat.class);
        conf.set(LearningConfiguration.XLEARNING_STREAM_EPOCH, String.valueOf(clientArguments.streamEpoch));
        conf.setBoolean(LearningConfiguration.XLEARNING_TF_EVALUATOR, clientArguments.tfEvaluator);

        if (clientArguments.confs != null) {
            setConf(conf, clientArguments, appMasterUserEnv, containerUserEnv);
            if (containerUserEnv.size() > 0) {
                StringBuilder userEnv = new StringBuilder();
                for (String key : containerUserEnv.keySet()) {
                    userEnv.append(key);
                    userEnv.append("=");
                    userEnv.append(containerUserEnv.get(key));
                    userEnv.append("|");
                }
                conf.set(LearningConfiguration.XLEARNING_CONTAINER_EXTRAENV, userEnv.deleteCharAt(userEnv.length() - 1).toString());
            }
        }

        if ("MPI".equals(clientArguments.appType)) {
            conf.setBoolean(LearningConfiguration.XLEARNING_TF_BOARD_ENABLE, false);
            conf.set(LearningConfiguration.XLEARNING_CONTAINER_TYPE, LearningConfiguration.DEFAULT_XLEARNING_CONTAINER_TYPE);
        }

        if (conf.getInt(LearningConfiguration.XLEARNING_PS_NUM, LearningConfiguration.DEFAULT_XLEARNING_PS_NUM) == 0) {
            if (("TENSORFLOW".equals(clientArguments.appType) || "KERAS".equals(clientArguments.appType) || "PYTORCH".equals(clientArguments.appType))
//                    && conf.getBoolean(LearningConfiguration.XLEARNING_TF_DISTRIBUTION_STRATEGY, LearningConfiguration.DEFAULT_XLEARNING_TF_DISTRIBUTION_STRATEGY)
                    && conf.getInt(LearningConfiguration.XLEARNING_WORKER_NUM, LearningConfiguration.DEFAULT_XLEARNING_WORKER_NUM) > 1) {
                conf.setBoolean(LearningConfiguration.XLEARNING_MODE_SINGLE, false);
            } else {
                conf.setBoolean(LearningConfiguration.XLEARNING_MODE_SINGLE, true);
            }
        } else {
            conf.setBoolean(LearningConfiguration.XLEARNING_MODE_SINGLE, false);
        }

        if (conf.getInt(LearningConfiguration.XLEARNING_WORKER_NUM, LearningConfiguration.DEFAULT_XLEARNING_WORKER_NUM) == 1) {
            conf.setInt(LearningConfiguration.XLEARNING_TF_BOARD_WORKER_INDEX, 0);
        }

        if (conf.getBoolean(LearningConfiguration.XLEARNING_TF_EVALUATOR, LearningConfiguration.DEFAULT_XLEARNING_TF_EVALUATOR)) {
            if (("TENSORFLOW".equals(clientArguments.appType.toUpperCase()) || "KERAS".equals(clientArguments.appType.toUpperCase()) || "PYTORCH".equals(clientArguments.appType.toUpperCase())) && !conf.getBoolean(LearningConfiguration.XLEARNING_MODE_SINGLE, true) && conf.getInt(LearningConfiguration.XLEARNING_WORKER_NUM, LearningConfiguration.DEFAULT_XLEARNING_WORKER_NUM) > 1) {
                LOG.info("Current job has the evaluator.");
            } else {
                conf.setBoolean(LearningConfiguration.XLEARNING_TF_EVALUATOR, LearningConfiguration.DEFAULT_XLEARNING_TF_EVALUATOR);
            }
        }

        if (conf.get(LearningConfiguration.XLEARNING_TF_BOARD_LOG_DIR, LearningConfiguration.DEFAULT_XLEARNING_TF_BOARD_LOG_DIR).indexOf("/") == 0) {
            Path tf_board_log_dir = new Path(conf.get("fs.defaultFS"), conf.get(LearningConfiguration.XLEARNING_TF_BOARD_LOG_DIR));
            conf.set(LearningConfiguration.XLEARNING_TF_BOARD_LOG_DIR, tf_board_log_dir.toString());
        }
        if ((conf.get(LearningConfiguration.XLEARNING_TF_BOARD_LOG_DIR).indexOf("hdfs") == 0) && (!"TENSORFLOW".equals(clientArguments.appType) && !"KERAS".equals(clientArguments.appType) && !"PYTORCH".equals(clientArguments.appType))) {
            LOG.warn("VisualDL not support the hdfs path for logdir. Please ensure the logdir setting is right.");
        }

        if (clientArguments.extraConfs != null) {
            @SuppressWarnings("unchecked")
            Enumeration<String> confSet = (Enumeration<String>) clientArguments.extraConfs.propertyNames();
            while (confSet.hasMoreElements()) {
                String confArg = confSet.nextElement();
                conf.set(confArg, clientArguments.extraConfs.getProperty(confArg));
            }
        }

        return conf;
    }

    private static void showWelcome() {
        System.err.println("Welcome to\n " +
                "\t__   ___                           _\n" +
                "\t\\ \\ / / |                         (_) \n" +
                "\t \\ V /| |     ___  __ _ _ __ _ __  _ _ __   __ _ \n" +
                "\t  > < | |    / _ \\/ _` | '__| '_ \\| | '_ \\ / _` |\n" +
                "\t / . \\| |___|  __/ (_| | |  | | | | | | | | (_| |\n" +
                "\t/_/ \\_\\______\\___|\\__,_|_|  |_| |_|_|_| |_|\\__, |\n" +
                "\t                                            __/ |\n" +
                "\t                                           |___/ \n"
        );
    }

    private void setConf(YarnConfiguration conf, ClientArguments clientArguments, Map<String, String> appMasterUserEnv, Map<String, String> containerUserEnv) {
        Enumeration<String> confSet = (Enumeration<String>) clientArguments.confs.propertyNames();
        while (confSet.hasMoreElements()) {
            String confArg = confSet.nextElement();
            if (confArg.startsWith(LearningConstants.AM_ENV_PREFIX)) {
                String key = confArg.substring(LearningConstants.AM_ENV_PREFIX.length()).trim();
                Utilities.addPathToEnvironment(appMasterUserEnv, key, clientArguments.confs.getProperty(confArg).trim());
            } else if (confArg.startsWith(LearningConstants.CONTAINER_ENV_PREFIX)) {
                String key = confArg.substring(LearningConstants.CONTAINER_ENV_PREFIX.length()).trim();
                Utilities.addPathToEnvironment(containerUserEnv, key, clientArguments.confs.getProperty(confArg).trim());
            } else {
                conf.set(confArg, clientArguments.confs.getProperty(confArg));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void assignOutput(ClientArguments clientArguments, ConcurrentHashMap<String, String> outputPaths) throws IOException {
        Enumeration<String> outputs = (Enumeration<String>) clientArguments.outputs.propertyNames();
        while (outputs.hasMoreElements()) {
            String outputRemote = outputs.nextElement();
            String outputLocal = clientArguments.outputs.getProperty(outputRemote);
            if (outputLocal.equals("true")) {
                outputLocal = conf.get(LearningConfiguration.XLEARNING_OUTPUT_LOCAL_DIR, LearningConfiguration.DEFAULT_XLEARNING_OUTPUT_LOCAL_DIR);
                LOG.info("Remote output path: " + outputRemote + " not defined the local output path. Default path: output.");
            }
            Path path = new Path(outputRemote);
            if (path.getFileSystem(conf).exists(path)) {
                throw new IOException("Output path " + path + " already existed!");
            }
            if (outputPaths.containsKey(outputLocal)) {
                outputPaths.put(outputLocal, outputPaths.get(outputLocal) + "," + outputRemote);
            } else {
                outputPaths.put(outputLocal, outputRemote);
            }
            LOG.info("Local output path: " + outputLocal + " and remote output path: " + outputRemote);
        }
    }

    @SuppressWarnings("unchecked")
    private void assignInput(ClientArguments clientArguments, ConcurrentHashMap<String, String> inputPaths) throws IOException {
        Enumeration<String> inputs = (Enumeration<String>) clientArguments.inputs.propertyNames();
        while (inputs.hasMoreElements()) {
            String inputRemote = inputs.nextElement();
            String inputLocal = clientArguments.inputs.getProperty(inputRemote);
            if (inputLocal.equals("true")) {
                inputLocal = "input";
            }
            for (String pathdir : StringUtils.split(inputRemote, ",")) {
                Path path = new Path(pathdir);
                FileSystem fs = path.getFileSystem(conf);
                FileStatus[] pathStatus = fs.globStatus(path);
                if (pathStatus == null || pathStatus.length <= 0) {
                    throw new IOException("Input path " + path + "not existed!");
                }
            }
            if (inputPaths.containsKey(inputLocal)) {
                inputPaths.put(inputLocal, inputPaths.get(inputLocal) + "," + inputRemote);
            } else {
                inputPaths.put(inputLocal, inputRemote);
            }
            LOG.info("Local input path: " + inputLocal + " and remote input path: " + inputRemote);
        }
    }

    private static ApplicationReport getApplicationReport(ApplicationId appId, YarnClient yarnClient)
            throws YarnException, IOException {
        return yarnClient.getApplicationReport(appId);
    }

    private static ApplicationMessageProtocol getAppMessageHandler(
            YarnConfiguration conf, String appMasterAddress, int appMasterPort) throws IOException {
        ApplicationMessageProtocol appMessageHandler = null;
        if (!StringUtils.isBlank(appMasterAddress) && !appMasterAddress.equalsIgnoreCase("N/A")) {
            InetSocketAddress addr = new InetSocketAddress(appMasterAddress, appMasterPort);
            appMessageHandler = RPC.getProxy(ApplicationMessageProtocol.class, ApplicationMessageProtocol.versionID, addr, conf);
        }
        return appMessageHandler;
    }

    private void checkArguments(YarnConfiguration conf, GetNewApplicationResponse newApplication, ClientArguments clientArguments) {
        int maxMem = newApplication.getMaximumResourceCapability().getMemory();
        LOG.info("Max mem capability of resources in this cluster " + maxMem);
        int maxVCores = newApplication.getMaximumResourceCapability().getVirtualCores();
        LOG.info("Max vcores capability of resources in this cluster " + maxVCores);

//        long maxGCores = newApplication.getMaximumResourceCapability().getResourceValue(LearningConstants.GPU);
//        LOG.info("Max gpu cores capability of resources in this cluster " + maxGCores);

        int amMem = conf.getInt(LearningConfiguration.XLEARNING_AM_MEMORY, LearningConfiguration.DEFAULT_XLEARNING_AM_MEMORY);
        int amCores = conf.getInt(LearningConfiguration.XLEARNING_AM_CORES, LearningConfiguration.DEFAULT_XLEARNING_AM_CORES);
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

        int workerNum = conf.getInt(LearningConfiguration.XLEARNING_WORKER_NUM, LearningConfiguration.DEFAULT_XLEARNING_WORKER_NUM);
        int workerMemory = conf.getInt(LearningConfiguration.XLEARNING_WORKER_MEMORY, LearningConfiguration.DEFAULT_XLEARNING_WORKER_MEMORY);
        int workerVcores = conf.getInt(LearningConfiguration.XLEARNING_WORKER_VCORES, LearningConfiguration.DEFAULT_XLEARNING_WORKER_VCORES);
        long workerGcores = conf.getLong(LearningConfiguration.XLEARNING_WORKER_GPU, LearningConfiguration.DEFAULT_XLEARNING_WORKER_GPU);
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

        int chiefWorkerMemory = conf.getInt(LearningConfiguration.XLEARNING_CHIEF_WORKER_MEMORY, LearningConfiguration.DEFAULT_XLEARNING_WORKER_MEMORY);
        if (chiefWorkerMemory != workerMemory) {
            if (chiefWorkerMemory > maxMem) {
                throw new RequestOverLimitException("Chief Worker memory requested " + chiefWorkerMemory +
                        " above the max threshold of yarn cluster " + maxMem);
            }
            if (chiefWorkerMemory <= 0) {
                throw new IllegalArgumentException(
                        "Invalid memory specified for chief worker, exiting."
                                + "Specified memory=" + chiefWorkerMemory);
            }
            LOG.info("Apply for chief worker Memory " + chiefWorkerMemory + "M");
        }

        int evaluatorWorkerMemory = conf.getInt(LearningConfiguration.XLEARNING_EVALUATOR_WORKER_MEMORY, LearningConfiguration.DEFAULT_XLEARNING_WORKER_MEMORY);
        if (evaluatorWorkerMemory != workerMemory && conf.getBoolean(LearningConfiguration.XLEARNING_TF_EVALUATOR, LearningConfiguration.DEFAULT_XLEARNING_TF_EVALUATOR)) {
            if (evaluatorWorkerMemory > maxMem) {
                throw new RequestOverLimitException("Evaluator Worker memory requested " + evaluatorWorkerMemory +
                        " above the max threshold of yarn cluster " + maxMem);
            }
            if (evaluatorWorkerMemory <= 0) {
                throw new IllegalArgumentException(
                        "Invalid memory specified for evaluator worker, exiting."
                                + "Specified memory=" + evaluatorWorkerMemory);
            }
            LOG.info("Apply for evaluator worker Memory " + evaluatorWorkerMemory + "M");
        }

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

//        if (workerGcores > maxGCores) {
//            throw new RequestOverLimitException("Worker gpu cores requested " + workerGcores +
//                    " above the max threshold of yarn cluster " + maxGCores);
//        }
//        if (workerGcores < 0) {
//            throw new IllegalArgumentException(
//                    "Invalid gpu cores specified for worker, exiting."
//                            + "Specified gpu cores=" + workerGcores);
//        }
//        LOG.info("Apply for worker gpu cores " + workerGcores);

        if ("TENSORFLOW".equals(clientArguments.appType) || "KERAS".equals(clientArguments.appType) || "PYTORCH".equals(clientArguments.appType) || "MXNET".equals(clientArguments.appType) || "LIGHTLDA".equals(clientArguments.appType) || "XFLOW".equals(clientArguments.appType)) {
            Boolean single = conf.getBoolean(LearningConfiguration.XLEARNING_MODE_SINGLE, LearningConfiguration.DEFAULT_XLEARNING_MODE_SINGLE);
            int psNum = conf.getInt(LearningConfiguration.XLEARNING_PS_NUM, LearningConfiguration.DEFAULT_XLEARNING_PS_NUM);
            if (psNum < 0) {
                throw new IllegalArgumentException(
                        "Invalid no. of ps specified, exiting."
                                + " Specified container number=" + psNum);
            }
            LOG.info("Apply for ps number " + psNum);
            if (!single) {
                int psMemory = conf.getInt(LearningConfiguration.XLEARNING_PS_MEMORY, LearningConfiguration.DEFAULT_XLEARNING_PS_MEMORY);
                int psVcores = conf.getInt(LearningConfiguration.XLEARNING_PS_VCORES, LearningConfiguration.DEFAULT_XLEARNING_PS_VCORES);
                long psGcores = conf.getLong(LearningConfiguration.XLEARNING_PS_GPU, LearningConfiguration.DEFAULT_XLEARNING_PS_GPU);
                if (psMemory > maxMem) {
                    throw new RequestOverLimitException("ps memory requested " + psMemory +
                            " above the max threshold of yarn cluster " + maxMem);
                }
                if (psMemory <= 0) {
                    throw new IllegalArgumentException(
                            "Invalid memory specified for ps, exiting."
                                    + "Specified memory=" + psMemory);
                }
                LOG.info("Apply for ps Memory " + psMemory + "M");
                if (psVcores > maxVCores) {
                    throw new RequestOverLimitException("ps vcores requested " + psVcores +
                            " above the max threshold of yarn cluster " + maxVCores);
                }
                if (psVcores <= 0) {
                    throw new IllegalArgumentException(
                            "Invalid vcores specified for ps, exiting."
                                    + "Specified vcores=" + psVcores);
                }
                LOG.info("Apply for ps vcores " + psVcores);

//                if (psGcores > maxGCores) {
//                    throw new RequestOverLimitException("ps gpu cores requested " + psGcores +
//                            " above the max threshold of yarn cluster " + maxGCores);
//                }
                if (psGcores < 0) {
                    throw new IllegalArgumentException(
                            "Invalid gpu cores specified for ps, exiting."
                                    + "Specified gpu cores=" + psGcores);
                }
                LOG.info("Apply for ps gpu cores " + psGcores);
            }
            int limitNode = conf.getInt(LearningConfiguration.XLEARNING_EXECUTE_NODE_LIMIT, LearningConfiguration.DEFAULT_XLEARNING_EXECUTENODE_LIMIT);
            if (workerNum + psNum > limitNode) {
                throw new RequestOverLimitException("Container num requested over the limit " + limitNode);
            }
        }
    }

    public String submit(String[] args) throws IOException, YarnException, ParseException, ClassNotFoundException {

        ClientArguments clientArguments = new ClientArguments(args);
        Map<String, String> appMasterUserEnv = new HashMap<>();
        Map<String, String> containerUserEnv = new HashMap<>();
        ConcurrentHashMap<String, String> inputPaths = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> outputPaths = new ConcurrentHashMap<>();

        YarnConfiguration conf = init(clientArguments, appMasterUserEnv, containerUserEnv);

        LOG.info("Requesting a new application from cluster with " + yarnClient.getYarnClusterMetrics().getNumNodeManagers() + " NodeManagers");
        YarnClientApplication newAPP = yarnClient.createApplication();

        if (clientArguments.inputs != null) {
            assignInput(clientArguments, inputPaths);
        }

        if (clientArguments.outputs != null) {
            assignOutput(clientArguments, outputPaths);
        }

        if (clientArguments.xlearningCacheFiles != null) {
            String[] cacheFiles = StringUtils.split(clientArguments.xlearningCacheFiles, ",");
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
        }

        if (clientArguments.xlearningCacheArchives != null) {
            String[] cacheArchives = StringUtils.split(clientArguments.xlearningCacheArchives, ",");
            for (String path : cacheArchives) {
                Path pathRemote;
                if (path.contains("#")) {
                    String[] paths = StringUtils.split(path, "#");
                    if (paths.length != 2) {
                        throw new RuntimeException("Error cacheArchives path format " + path);
                    }
                    pathRemote = new Path(paths[0]);
                } else {
                    pathRemote = new Path(path);
                }
                if (!pathRemote.getFileSystem(conf).exists(pathRemote)) {
                    throw new IOException("cacheArchive path " + pathRemote + " not existed!");
                }
            }
        }

        GetNewApplicationResponse newAppResponse = newAPP.getNewApplicationResponse();
        ApplicationId applicationId = newAppResponse.getApplicationId();
        LOG.info("Got new Application: " + applicationId.toString());

        Path jobConfPath = Utilities
                .getRemotePath(conf, applicationId, LearningConstants.XLEARNING_JOB_CONFIGURATION);
        FSDataOutputStream out =
                FileSystem.create(jobConfPath.getFileSystem(conf), jobConfPath,
                        new FsPermission(JOB_FILE_PERMISSION));
        conf.writeXml(out);
        out.close();
        Map<String, LocalResource> localResources = new HashMap<>();
        localResources.put(LearningConstants.XLEARNING_JOB_CONFIGURATION,
                Utilities.createApplicationResource(getFileSystem(), jobConfPath, LocalResourceType.FILE));

        checkArguments(conf, newAppResponse, clientArguments);

        ApplicationSubmissionContext applicationContext = newAPP.getApplicationSubmissionContext();
        applicationContext.setApplicationId(applicationId);
        applicationContext.setApplicationName(clientArguments.appName);
        applicationContext.setApplicationType(clientArguments.appType);
        Path appJarSrc = new Path(clientArguments.appMasterJar);
        Path appJarDst = Utilities
                .getRemotePath(conf, applicationId, LearningConstants.XLEARNING_APPLICATION_JAR);
        LOG.info("Copying " + appJarSrc + " to remote path " + appJarDst.toString());
        getFileSystem().copyFromLocalFile(false, true, appJarSrc, appJarDst);

        localResources.put(LearningConstants.XLEARNING_APPLICATION_JAR,
                Utilities.createApplicationResource(getFileSystem(), appJarDst, LocalResourceType.FILE));

        LOG.info("Building environments for the application master");
        Map<String, String> appMasterEnv = new HashMap<>();
        appMasterEnv.put(LearningConstants.Environment.XLEARNING_APP_NAME.toString(), clientArguments.appName);
        if (clientArguments.appType != null && !clientArguments.appType.equals("")) {
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_APP_TYPE.toString(), clientArguments.appType);
        } else {
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_APP_TYPE.toString(), LearningConfiguration.DEFAULT_XLEARNING_APP_TYPE.toUpperCase());
        }
        if (clientArguments.xlearningFiles != null) {
            Path[] xlearningFilesDst = new Path[clientArguments.xlearningFiles.length];
            LOG.info("Copy xlearning files from local filesystem to remote.");
            StringBuffer appFilesRemotePath = new StringBuffer(1000);
            for (int i = 0; i < clientArguments.xlearningFiles.length; i++) {
                assert (!clientArguments.xlearningFiles[i].isEmpty());

                if (!clientArguments.xlearningFiles[i].startsWith("hdfs:")) { //local
                    Path xlearningFilesSrc = new Path(clientArguments.xlearningFiles[i]);
                    xlearningFilesDst[i] = Utilities.getRemotePath(
                            conf, applicationId, new Path(clientArguments.xlearningFiles[i]).getName());
                    LOG.info("Copying " + clientArguments.xlearningFiles[i] + " to remote path " + xlearningFilesDst[i].toString());
                    getFileSystem().copyFromLocalFile(false, true, xlearningFilesSrc, xlearningFilesDst[i]);
                    appFilesRemotePath.append(xlearningFilesDst[i].toUri().toString()).append(",");
                } else { //hdfs
                    Path pathRemote = new Path(clientArguments.xlearningFiles[i]);
                    if (!pathRemote.getFileSystem(conf).exists(pathRemote)) {
                        throw new IOException("hdfs xlearningFiles path " + pathRemote + " not existed!");
                    }
                    appFilesRemotePath.append(clientArguments.xlearningFiles[i]).append(",");
                }
            }
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_FILES_LOCATION.toString(),
                    appFilesRemotePath.deleteCharAt(appFilesRemotePath.length() - 1).toString());

            if ((clientArguments.appType.equals("MXNET") || clientArguments.appType.equals("XFLOW")) && !conf.getBoolean(LearningConfiguration.XLEARNING_MODE_SINGLE, LearningConfiguration.DEFAULT_XLEARNING_MODE_SINGLE)) {
                String appFilesRemoteLocation = appMasterEnv.get(LearningConstants.Environment.XLEARNING_FILES_LOCATION.toString());
                String[] xlearningFiles = StringUtils.split(appFilesRemoteLocation, ",");
                for (String file : xlearningFiles) {
                    Path path = new Path(file);
                    localResources.put(path.getName(),
                            Utilities.createApplicationResource(path.getFileSystem(conf),
                                    path,
                                    LocalResourceType.FILE));
                }
            }
        }

        String libJarsClassPath = "";
        if (clientArguments.libJars != null) {
            Path[] jarFilesDst = new Path[clientArguments.libJars.length];
            LOG.info("Copy XLearning lib jars from local filesystem to remote.");
            StringBuffer appLibJarsRemotePath = new StringBuffer(1000);
            for (int i = 0; i < clientArguments.libJars.length; i++) {
                assert (!clientArguments.libJars[i].isEmpty());
                if (!clientArguments.libJars[i].startsWith("hdfs://")) {
                    Path jarFilesSrc = new Path(clientArguments.libJars[i]);
                    jarFilesDst[i] = Utilities.getRemotePath(
                            conf, applicationId, new Path(clientArguments.libJars[i]).getName());
                    LOG.info("Copying " + clientArguments.libJars[i] + " to remote path " + jarFilesDst[i].toString());
                    getFileSystem().copyFromLocalFile(false, true, jarFilesSrc, jarFilesDst[i]);
                    appLibJarsRemotePath.append(jarFilesDst[i].toUri().toString()).append(",");
                } else {
                    Path pathRemote = new Path(clientArguments.libJars[i]);
                    if (!pathRemote.getFileSystem(conf).exists(pathRemote)) {
                        throw new IOException("hdfs lib jars path " + pathRemote + " not existed!");
                    }
                    appLibJarsRemotePath.append(clientArguments.libJars[i]).append(",");
                }
            }

            String appFilesRemoteLocation = appLibJarsRemotePath.deleteCharAt(appLibJarsRemotePath.length() - 1).toString();
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_LIBJARS_LOCATION.toString(),
                    appFilesRemoteLocation);

            String[] jarFiles = StringUtils.split(appFilesRemoteLocation, ",");
            for (String file : jarFiles) {
                Path path = new Path(file);
                localResources.put(path.getName(),
                        Utilities.createApplicationResource(path.getFileSystem(conf),
                                path,
                                LocalResourceType.FILE));
                libJarsClassPath += path.getName() + ":";
            }
        }
        StringBuilder classPathEnv = new StringBuilder("${CLASSPATH}:./*");
        for (String cp : conf.getStrings(LearningConfiguration.YARN_APPLICATION_CLASSPATH,
                LearningConfiguration.DEFAULT_XLEARNING_APPLICATION_CLASSPATH)) {
            classPathEnv.append(':');
            classPathEnv.append(cp.trim());
        }

        if (conf.getBoolean(LearningConfiguration.XLEARNING_USER_CLASSPATH_FIRST, LearningConfiguration.DEFAULT_XLEARNING_USER_CLASSPATH_FIRST)) {
            appMasterEnv.put("CLASSPATH", libJarsClassPath + classPathEnv.toString());
        } else {
            appMasterEnv.put("CLASSPATH", classPathEnv.toString() + ":" + libJarsClassPath);
        }

        /**
         * set app env
         */
        String appEnv = clientArguments.appEnv;
        if (StringUtils.isNotBlank(appEnv)) {
            LOG.info("app env : " + appEnv);
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_APP_ENV.toString(), appEnv);
        }

        appMasterEnv.put(LearningConstants.Environment.XLEARNING_STAGING_LOCATION.toString(), Utilities
                .getRemotePath(conf, applicationId, "").toString());

        appMasterEnv.put(LearningConstants.Environment.APP_JAR_LOCATION.toString(), appJarDst.toUri().toString());
        appMasterEnv.put(LearningConstants.Environment.XLEARNING_JOB_CONF_LOCATION.toString(), jobConfPath.toString());

        if (clientArguments.launchCmd != null && !clientArguments.launchCmd.equals("")) {
            String launchCmd = new LaunchCommandBuilder(clientArguments, conf).buildCmd();
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_EXEC_CMD.toString(),
                    clientArguments.launchCmd);
        } else if (clientArguments.containerType.equals("docker")) {
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_EXEC_CMD.toString(), "");
        } else {
            throw new IllegalArgumentException("Invalid launch cmd for the application");
        }

        if (clientArguments.xlearningCacheFiles != null && !clientArguments.xlearningCacheFiles.equals("")) {
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_CACHE_FILE_LOCATION.toString(), clientArguments.xlearningCacheFiles);
            if (((clientArguments.appType.equals("MXNET") || clientArguments.appType.equals("XFLOW")) && !conf.getBoolean(LearningConfiguration.XLEARNING_MODE_SINGLE, LearningConfiguration.DEFAULT_XLEARNING_MODE_SINGLE))
                    || clientArguments.appType.equals("DISTXGBOOST")) {
                URI defaultUri = new Path(conf.get("fs.defaultFS")).toUri();
                LOG.info("default URI is " + defaultUri.toString());
                String appCacheFilesRemoteLocation = appMasterEnv.get(LearningConstants.Environment.XLEARNING_CACHE_FILE_LOCATION.toString());
                String[] cacheFiles = StringUtils.split(appCacheFilesRemoteLocation, ",");
                for (String path : cacheFiles) {
                    Path pathRemote;
                    String aliasName;
                    if (path.contains("#")) {
                        String[] paths = StringUtils.split(path, "#");
                        if (paths.length != 2) {
                            throw new RuntimeException("Error cacheFile path format " + appCacheFilesRemoteLocation);
                        }
                        pathRemote = new Path(paths[0]);
                        aliasName = paths[1];
                    } else {
                        pathRemote = new Path(path);
                        aliasName = pathRemote.getName();
                    }
                    URI pathRemoteUri = pathRemote.toUri();

                    if (pathRemoteUri.getScheme() == null || pathRemoteUri.getHost() == null) {
                        pathRemote = new Path(defaultUri.toString(), pathRemote.toString());
                    }

                    LOG.info("Cache file remote path is " + pathRemote + " and alias name is " + aliasName);
                    localResources.put(aliasName,
                            Utilities.createApplicationResource(pathRemote.getFileSystem(conf),
                                    pathRemote,
                                    LocalResourceType.FILE));
                }
            }
        }

        if (clientArguments.xlearningCacheArchives != null && !clientArguments.xlearningCacheArchives.equals("")) {
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_CACHE_ARCHIVE_LOCATION.toString(), clientArguments.xlearningCacheArchives);

            if (((clientArguments.appType.equals("MXNET") || clientArguments.appType.equals("XFLOW")) && !conf.getBoolean(LearningConfiguration.XLEARNING_MODE_SINGLE, LearningConfiguration.DEFAULT_XLEARNING_MODE_SINGLE))
                    || clientArguments.appType.equals("DISTXGBOOST")) {
                URI defaultUri = new Path(conf.get("fs.defaultFS")).toUri();
                String appCacheArchivesRemoteLocation = appMasterEnv.get(LearningConstants.Environment.XLEARNING_CACHE_ARCHIVE_LOCATION.toString());
                String[] cacheArchives = StringUtils.split(appCacheArchivesRemoteLocation, ",");
                for (String path : cacheArchives) {
                    Path pathRemote;
                    String aliasName;
                    if (path.contains("#")) {
                        String[] paths = StringUtils.split(path, "#");
                        if (paths.length != 2) {
                            throw new RuntimeException("Error cacheArchive path format " + appCacheArchivesRemoteLocation);
                        }
                        pathRemote = new Path(paths[0]);
                        aliasName = paths[1];
                    } else {
                        pathRemote = new Path(path);
                        aliasName = pathRemote.getName();
                    }
                    URI pathRemoteUri = pathRemote.toUri();

                    if (pathRemoteUri.getScheme() == null || pathRemoteUri.getHost() == null) {
                        pathRemote = new Path(defaultUri.toString(), pathRemote.toString());
                    }
                    LOG.info("CacheArchive remote path is " + pathRemote + " and alias name is " + aliasName);
                    localResources.put(aliasName,
                            Utilities.createApplicationResource(pathRemote.getFileSystem(conf),
                                    pathRemote,
                                    LocalResourceType.ARCHIVE));
                }
            }
        }

        Set<String> inputPathKeys = inputPaths.keySet();
        StringBuilder inputLocation = new StringBuilder(1000);
        if (inputPathKeys.size() > 0) {
            for (String key : inputPathKeys) {
                inputLocation.append(inputPaths.get(key)).
                        append("#").
                        append(key).
                        append("|");
            }
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_INPUTS.toString(),
                    inputLocation.deleteCharAt(inputLocation.length() - 1).toString());
        }

        Set<String> outputPathKeys = outputPaths.keySet();
        StringBuilder outputLocation = new StringBuilder(1000);
        if (outputPathKeys.size() > 0) {
            for (String key : outputPathKeys) {
                for (String value : StringUtils.split(outputPaths.get(key), ",")) {
                    outputLocation.append(value).
                            append("#").
                            append(key).
                            append("|");
                }
            }
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_OUTPUTS.toString(),
                    outputLocation.deleteCharAt(outputLocation.length() - 1).toString());
        }

        appMasterEnv.put(LearningConstants.Environment.XLEARNING_CONTAINER_MAX_MEMORY.toString(), String.valueOf(newAppResponse.getMaximumResourceCapability().getMemory()));

        if (clientArguments.userPath != null && !clientArguments.userPath.equals("")) {
            appMasterEnv.put(LearningConstants.Environment.USER_PATH.toString(), clientArguments.userPath);
        }

        if (clientArguments.userLD_LIBRARY_PATH != null && !clientArguments.userLD_LIBRARY_PATH.equals("")) {
            appMasterEnv.put(LearningConstants.Environment.USER_LD_LIBRARY_PATH.toString(), clientArguments.userLD_LIBRARY_PATH);
        }

        if (clientArguments.outputIndex >= 0) {
            appMasterEnv.put(LearningConstants.Environment.XLEARNING_OUTPUTS_WORKER_INDEX.toString(), String.valueOf(clientArguments.outputIndex));
        }

        if (appMasterUserEnv.size() > 0) {
            for (String envKey : appMasterUserEnv.keySet()) {
                Utilities.addPathToEnvironment(appMasterEnv, envKey, appMasterUserEnv.get(envKey));
            }
        }

        LOG.info("Building application master launch command");
        List<String> appMasterArgs = new ArrayList<>(20);
        appMasterArgs.add("${JAVA_HOME}" + "/bin/java");
        appMasterArgs.add("-Xms" + conf.getInt(LearningConfiguration.XLEARNING_AM_MEMORY, LearningConfiguration.DEFAULT_XLEARNING_AM_MEMORY) + "m");
        appMasterArgs.add("-Xmx" + conf.getInt(LearningConfiguration.XLEARNING_AM_MEMORY, LearningConfiguration.DEFAULT_XLEARNING_AM_MEMORY) + "m");
        String javaOpts = conf.get(LearningConfiguration.XLEARNING_APPMASTER_EXTRA_JAVA_OPTS, LearningConfiguration.DEFAULT_XLEARNING_APPMASTER_EXTRA_JAVA_OPTS);
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

        Resource capability = Records.newRecord(Resource.class);
        capability.setMemory(conf.getInt(LearningConfiguration.XLEARNING_AM_MEMORY, LearningConfiguration.DEFAULT_XLEARNING_AM_MEMORY));
        capability.setVirtualCores(conf.getInt(LearningConfiguration.XLEARNING_AM_CORES, LearningConfiguration.DEFAULT_XLEARNING_AM_CORES));
        applicationContext.setResource(capability);


        ByteBuffer tokenBuffer = null;
        if (null != baseConfig && baseConfig.isOpenKerberos()) {
            tokenBuffer = SecurityUtil.getDelegationTokens(conf, getYarnClient());
            setKrbResource(conf, applicationId, appMasterEnv, clientArguments.appType);
        }

        ContainerLaunchContext amContainer = ContainerLaunchContext.newInstance(
                localResources, appMasterEnv, appMasterLaunchcommands, null, tokenBuffer, null);
        applicationContext.setAMContainerSpec(amContainer);

        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(conf.getInt(LearningConfiguration.XLEARNING_APP_PRIORITY, LearningConfiguration.DEFAULT_XLEARNING_APP_PRIORITY));
        applicationContext.setPriority(priority);
        applicationContext.setQueue(conf.get(LearningConfiguration.XLEARNING_APP_QUEUE, LearningConfiguration.DEFAULT_XLEARNING_APP_QUEUE));
        String amNodeLabelExpression = conf.get(LearningConfiguration.XLEARNING_AM_NODELABELEXPRESSION);
        if (amNodeLabelExpression != null && amNodeLabelExpression.trim() != "") {
            try {
                Method method = applicationContext.getClass().getMethod("setNodeLabelExpression", String.class);
                method.invoke(applicationContext, amNodeLabelExpression);
            } catch (Exception e) {
                LOG.warn("Set am node label expression error: " + e);
            }
        }

        try {
            LOG.info("Submitting application to ResourceManager");
            applicationId = yarnClient.submitApplication(applicationContext);
            return applicationId.toString();
        } catch (YarnException e) {
            throw new RuntimeException("Application submit failed! Exception: " + e);
        }

    }

    /**
     * 上传keytab、krb5.conf、py4j-gateway-server jar文件，供给Worker容器使用。
     * @param yarnConf
     * @param applicationId
     * @param appMasterEnv
     * @throws IOException
     */
    private void setKrbResource(YarnConfiguration yarnConf, ApplicationId applicationId, Map<String, String> appMasterEnv, String appType) throws IOException {
        String[] krbPaths = KerberosUtils.getKerberosFile(baseConfig, null);
        Path localKeytabPath = new Path(krbPaths[0]);
        Path localKrb5Path = new Path(krbPaths[1]);

        YarnFileUploader fileUploader = new YarnFileUploader(yarnConf, applicationId);
        fileUploader.uploadSingleResource(localKeytabPath, LearningConstants.LOCALIZED_KEYTAB_PATH);
        fileUploader.uploadSingleResource(localKrb5Path, LearningConstants.LOCALIZED_KR5B_PATH);
        appMasterEnv.put(LearningConstants.ENV_PRINCIPAL, baseConfig.getPrincipal());


        final String commonPath = "/common/";
        String py4jJar = this.appJarSrc.getParent().getParent().toString() + commonPath + LearningConstants.PYTHON_GATEWAY_PATH;
        LOG.info("py4j gateway server jar path: " + py4jJar);
        Path localPy4jJarPath = new Path(py4jJar);
        fileUploader.uploadSingleResource(localPy4jJarPath, LearningConstants.LOCALIZED_GATEWAY_PATH);
    }

    class YarnFileUploader {

        private YarnConfiguration conf;
        private ApplicationId applicationId;

        public YarnFileUploader(YarnConfiguration conf, ApplicationId applicationId) {
            this.conf = conf;
            this.applicationId = applicationId;
        }

        public void uploadSingleResource(Path localPath, String remoteFileName) throws IOException {
            Path remotePath = Utilities.getRemotePath(this.conf, this.applicationId, remoteFileName);
            uploadLocalFileToRemote(localPath, remotePath);
        }

        private void uploadLocalFileToRemote(Path srcPath, Path dstPath) throws IOException {
            LOG.info("Copying "+ srcPath +" to remote path " + dstPath);
            getFileSystem().copyFromLocalFile(false, true, srcPath, dstPath);
        }
    }

    private boolean waitCompleted(ApplicationId applicationId) throws IOException, YarnException {
        ApplicationMessageProtocol xlearningClient = null;
        ApplicationReport applicationReport = getApplicationReport(applicationId, yarnClient);
        LOG.info("The url to track the job: " + applicationReport.getTrackingUrl());
        AtomicBoolean isRunning = new AtomicBoolean(true);
        while (true) {
            assert (applicationReport != null);
            if (xlearningClient == null && isRunning.get()) {
                LOG.info("Application report for " + applicationId +
                        " (state: " + applicationReport.getYarnApplicationState().toString() + ")");
                xlearningClient = getAppMessageHandler(conf, applicationReport.getHost(),
                        applicationReport.getRpcPort());
            }

            YarnApplicationState yarnApplicationState = applicationReport.getYarnApplicationState();
            FinalApplicationStatus finalApplicationStatus = applicationReport.getFinalApplicationStatus();
            if (YarnApplicationState.FINISHED == yarnApplicationState) {
                xlearningClient = null;
                isRunning.set(false);
                if (FinalApplicationStatus.SUCCEEDED == finalApplicationStatus) {
                    return true;
                } else {
                    LOG.info("Application has completed failed with YarnApplicationState=" + yarnApplicationState.toString() +
                            " and FinalApplicationStatus=" + finalApplicationStatus.toString());
                    return false;
                }
            } else if (YarnApplicationState.KILLED == yarnApplicationState
                    || YarnApplicationState.FAILED == yarnApplicationState) {
                xlearningClient = null;
                isRunning.set(false);
                LOG.info("Application has completed with YarnApplicationState=" + yarnApplicationState.toString() +
                        " and FinalApplicationStatus=" + finalApplicationStatus.toString());
                return false;
            }

            if (xlearningClient != null) {
                try {
                    Message[] messages = xlearningClient.fetchApplicationMessages();
                    if (messages != null && messages.length > 0) {
                        for (Message message : messages) {
                            if (message.getLogType() == LogType.STDERR) {
                                LOG.info(message.getMessage());
                            } else {
                                System.out.println(message.getMessage());
                            }
                        }
                    }
                } catch (UndeclaredThrowableException e) {
                    xlearningClient = null;
                    LOG.warn("Connecting to ApplicationMaster failed, try again later ", e);
                }
            }

            int logInterval = conf.getInt(LearningConfiguration.XLEARNING_LOG_PULL_INTERVAL, LearningConfiguration.DEFAULT_XLEARNING_LOG_PULL_INTERVAL);
            Utilities.sleep(logInterval);
            applicationReport = getApplicationReport(applicationId, yarnClient);
        }
    }

    public void kill(String jobId) throws IOException, YarnException {
        ApplicationId appId = ConverterUtils.toApplicationId(jobId);
        yarnClient.killApplication(appId);
    }

    public ApplicationReport getApplicationReport(String jobId) throws IOException, YarnException {
        ApplicationId appId = ConverterUtils.toApplicationId(jobId);
        return yarnClient.getApplicationReport(appId);
    }

    public List<NodeReport> getNodeReports() throws IOException, YarnException {
        return yarnClient.getNodeReports(NodeState.RUNNING);
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
                    dfs = FileSystem.get(conf);
                }
            }
        }
        return dfs;
    }

    public YarnClient getYarnClient(){
        long startTime = System.currentTimeMillis();
        try {
            if (yarnClient == null) {
                synchronized (this) {
                    if (yarnClient == null) {
                        LOG.info("buildYarnClient!");
                        YarnClient tmpYarnClient = YarnClient.createYarnClient();
                        tmpYarnClient.init(conf);
                        tmpYarnClient.start();
                        yarnClient = tmpYarnClient;
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
            LOG.error("buildYarnClient![backup]", e);
            YarnClient tmpYarnClient = YarnClient.createYarnClient();
            tmpYarnClient.init(conf);
            tmpYarnClient.start();
            yarnClient = tmpYarnClient;
        } finally {
            long endTime= System.currentTimeMillis();
            LOG.info("cost getYarnClient start-time:"+ startTime +" end-time:"+ endTime +", cost: " +(endTime - startTime));
        }
        return yarnClient;
    }

    public static void main(String[] args) {
        showWelcome();
        String appId = "";
        Client client;
        try {
            LOG.info("Initializing Client");
            client = new Client(args);
            appId = client.submit(args);
            if (client.waitCompleted(ConverterUtils.toApplicationId(appId))) {
                LOG.info("Application completed successfully");
                System.exit(0);
            }
        } catch (Exception e) {
            LOG.error("Error running Client", e);
            System.exit(1);
        }
    }
}
