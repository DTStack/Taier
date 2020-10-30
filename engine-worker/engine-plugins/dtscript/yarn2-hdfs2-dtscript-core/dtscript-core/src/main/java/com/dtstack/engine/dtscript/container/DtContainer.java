package com.dtstack.engine.dtscript.container;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import com.dtstack.engine.dtscript.common.type.AbstractAppType;
import com.dtstack.engine.dtscript.api.ApplicationContainerProtocol;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import com.dtstack.engine.dtscript.common.ReturnValue;
import com.dtstack.engine.dtscript.common.type.DummyType;
import com.dtstack.engine.dtscript.util.DebugUtil;
import com.dtstack.engine.dtscript.util.Utilities;
import com.google.common.base.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DtContainer {

    private static final Log LOG = LogFactory.getLog(DtContainer.class);

    private DtYarnConfiguration conf;

    private ApplicationContainerProtocol amClient;

    private DtContainerId containerId;

    private Map<String, String> envs;

    private ContainerStatusNotifier containerStatusNotifier;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final FileSystem localFs;

    private FileSystem dfs;

    private final AbstractAppType appType;

    private final Map<String, Object> containerInfo;

    private int heartbeatInterval;

    private DtContainer() throws IOException {
        containerInfo = new HashMap<>();
        containerInfo.put("time", System.currentTimeMillis());

        this.conf = new DtYarnConfiguration();

        conf.addResource(new Path(DtYarnConstants.LEARNING_JOB_CONFIGURATION));

        localFs = FileSystem.getLocal(conf);

        LOG.info("user is " + conf.get("hadoop.job.ugi"));
        containerId = new DtContainerId(ConverterUtils.toContainerId(System
                .getenv(ApplicationConstants.Environment.CONTAINER_ID.name())));
        LOG.info("sub container id: " + containerId);

        this.heartbeatInterval = this.conf.getInt(DtYarnConfiguration.DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL);
        this.envs = System.getenv();
        if (envs.containsKey(DtYarnConstants.Environment.APP_TYPE.toString())) {
            String applicationType = envs.get(DtYarnConstants.Environment.APP_TYPE.toString()).toUpperCase();
            appType = AbstractAppType.fromString(applicationType);
        } else {
            appType = new DummyType();
        }

    }

    private void init() {
        LOG.info("DtContainer initializing");
        String appMasterHost = System.getenv(DtYarnConstants.Environment.APPMASTER_HOST.toString());
        int appMasterPort = Integer.valueOf(System.getenv(DtYarnConstants.Environment.APPMASTER_PORT.toString()));
        InetSocketAddress addr = new InetSocketAddress(appMasterHost, appMasterPort);
        try {
            LOG.info("appMasterHost:" + appMasterHost + ", port:" + appMasterPort);
            amClient = RPC.getProxy(ApplicationContainerProtocol.class,
                    ApplicationContainerProtocol.versionID, addr, conf);
            LocalRemotePath[] localRemotePaths = amClient.getOutputLocation();
            LOG.info("get localRemotePaths:" + localRemotePaths.length);

            this.dfs = FileSystem.get(conf);
        } catch (Exception e) {
            LOG.error("-----------", e);
            LOG.error("Connecting to ApplicationMaster " + appMasterHost + ":" + appMasterPort + " failed!");
            LOG.error("Container will suicide!");
            System.exit(1);
        }

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            containerInfo.put("host", inetAddress.getHostName());
            containerInfo.put("ip", inetAddress.getHostAddress());
        } catch (UnknownHostException e) {
            LOG.error("Container unknow host! e:{}", e);
        }

        containerStatusNotifier = new ContainerStatusNotifier(amClient, conf, containerId);
        containerStatusNotifier.reportContainerStatusNow(DtContainerStatus.INITIALIZING);
        containerStatusNotifier.start();
    }

    public Configuration getConf() {
        return this.conf;
    }

    public ApplicationContainerProtocol getAmClient() {
        return this.amClient;
    }

    private DtContainerId getContainerId() {
        return this.containerId;
    }

    private ReturnValue run() throws IOException, InterruptedException {

        Date now = new Date();
        containerStatusNotifier.setContainersStartTime(now.toString());
        containerStatusNotifier.reportContainerStatusNow(DtContainerStatus.RUNNING);

        List<String> envList = new ArrayList<>(20);
        appType.env(envList);

        String[] env = envList.toArray(new String[envList.size()]);
        String logDirs = envs.get(ApplicationConstants.Environment.LOG_DIRS.name());

        String logDir = getFirstLogFile(logDirs);

        //LOG_DIRS 是可以配置多个路径的,并且以逗号分隔
        String command = envs.get(DtYarnConstants.Environment.DT_EXEC_CMD.toString())
                + " 1>" + logDir
                + "/dtstdout.log 2>" + logDir + "/dterror.log";

        command = appType.cmdContainerExtra(command, conf, containerInfo);

        String[] cmd = {"bash", "--login", "-c", command};

        LOG.info("Executing command:" + command);
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(cmd, env);

        LOG.info("Executing command end");

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    uploadOutputFiles();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 3000, TimeUnit.MILLISECONDS);

        printContainerInfo();

        int code = -1;
        while (code == -1 && !containerStatusNotifier.getCompleted()) {
            Utilities.sleep(heartbeatInterval);
            try {
                code = process.exitValue();
            } catch (IllegalThreadStateException e) {
                LOG.debug("XLearning Process is running");
            }
        }

        LOG.info("container exitValue: " + code);

        String log = readFile(logDir + "/dterror.log");
        ReturnValue rValue = new ReturnValue(code, log);
        return rValue;

    }

    private String getFirstLogFile(String logFile) {

        if (Strings.isNullOrEmpty(logFile)) {
            return logFile;
        }

        String[] splitStr = logFile.split(",");
        if (splitStr.length == 0) {
            return logFile;
        }

        return splitStr[0];
    }


    private void reportFailedAndExit(String msg) {
        LOG.info("reportFailedAndExit: " + msg);
        if (msg == null || msg.length() == 0) {
            msg = "";
            LOG.warn("reportFailedAndExit， the msg is blank");
        }
        Date now = new Date();
        containerStatusNotifier.setContainerErrorMessage(msg);
        containerStatusNotifier.setContainersFinishTime(now.toString());
        containerStatusNotifier.reportContainerStatusNow(DtContainerStatus.FAILED);

        Utilities.sleep(heartbeatInterval);

        System.exit(1);
    }

    private void reportSucceededAndExit() {
        LOG.info("reportSucceededAndExit");
        Date now = new Date();
        containerStatusNotifier.setContainersFinishTime(now.toString());
        containerStatusNotifier.reportContainerStatusNow(DtContainerStatus.SUCCEEDED);

        Utilities.sleep(heartbeatInterval);

        System.exit(0);
    }


    public void uploadOutputFiles() throws IOException {
        LOG.info("uploadOutputFiles start");
        List<LocalRemotePath> outputs = Arrays.asList(amClient.getOutputLocation());
        for (LocalRemotePath s : outputs) {
            LOG.info("Output path: " + s.getLocalLocation() + ":" + s.getDfsLocation());
        }

        if (outputs.size() > 0) {
            for (LocalRemotePath outputInfo : outputs) {
                Path localPath = new Path(outputInfo.getLocalLocation());
                Path remotePath = new Path(outputInfo.getDfsLocation());
                if (dfs.exists(remotePath)) {
                    LOG.info("Container remote output path " + remotePath + " exists, so we has to delete is first.");
                    dfs.delete(remotePath);
                }
                if (localFs.exists(localPath)) {
                    dfs.copyFromLocalFile(false, false, localPath, remotePath);
                    String localAbsolutePath = new File(outputInfo.getLocalLocation()).getAbsolutePath();
                    String hostName = (InetAddress.getLocalHost()).getHostName();
                    if (hostName == null) {
                        hostName = "";
                    }
                    LOG.info(hostName + "Upload output " + localAbsolutePath + " to remote path " + remotePath + " finished.");
                }
            }
        }
        LOG.info("uploadOutputFiles end");
    }

    private void printContainerInfo() throws IOException {
        FSDataOutputStream stream = null;
        try {
            ContainerId cId = containerId.getContainerId();
            Path path = Utilities.getRemotePath(conf, cId.getApplicationAttemptId().getApplicationId(), cId.toString()+".out");
            if (dfs.exists(path)) {
                dfs.delete(path);
            }
            stream = FileSystem.create(path.getFileSystem(conf), path, new FsPermission(FsPermission.createImmutable((short) 0777)));
            stream.write(new ObjectMapper().writeValueAsString(containerInfo).getBytes(StandardCharsets.UTF_8));
            stream.write("\n".getBytes(StandardCharsets.UTF_8));
        } finally {
            IOUtils.closeStream(stream);
        }
    }

    private String readFile(String filePath){

        LOG.info("start read file");
        StringBuffer sb = new StringBuffer();
        String line;
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is, DtYarnConfiguration.UTF8));
            line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOG.error("read file failed");
            throw new RdosDefineException("", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    is.close();
                }
            } catch (IOException e) {
                LOG.error("close resource error", e);
                throw new RdosDefineException("", e);
            }
        }
        LOG.info("end read file");
        return sb.toString();
    }

    public static void main(String[] args) {

        DtContainer container = null;
        try {
            final DtContainer fcontainer = new DtContainer();
            container = fcontainer;
            UserGroupInformation ugi = SecurityUtil.setupUserGroupInformation();

            ugi.doAs((PrivilegedExceptionAction<Void>) () -> {

                fcontainer.init();
                ReturnValue response = fcontainer.run();
                if (response.getExitValue() == 0) {
                    LOG.info("DtContainer " + fcontainer.getContainerId().toString() + " finish successfully");
                    fcontainer.reportSucceededAndExit();
                } else if (response.getExitValue() == ContainerExecutor.ExitCode.FORCE_KILLED.getExitCode()
                        || response.getExitValue() == ContainerExecutor.ExitCode.TERMINATED.getExitCode()) {
                    LOG.warn("DtContainer run exited!");
                    fcontainer.reportFailedAndExit(response.getErrorLog());
                } else {
                    LOG.error("DtContainer run failed! error");
                    fcontainer.reportFailedAndExit(response.getErrorLog());
                }

                return null;
            });

        } catch (Throwable e) {
            LOG.error("Some errors has occurred during container running!", e);
            if (container != null){
                container.reportFailedAndExit(DebugUtil.stackTrace(e));
            }
        }

        Utilities.sleep(3000);
    }


}