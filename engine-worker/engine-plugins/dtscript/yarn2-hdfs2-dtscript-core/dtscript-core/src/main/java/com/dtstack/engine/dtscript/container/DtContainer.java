package com.dtstack.engine.dtscript.container;

import com.dtstack.engine.base.util.NetUtils;
import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.api.ApplicationContainerProtocol;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.AppEnvConstant;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import com.dtstack.engine.dtscript.common.ReturnValue;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import com.dtstack.engine.dtscript.common.type.AbstractAppType;
import com.dtstack.engine.dtscript.common.type.DummyType;
import com.dtstack.engine.dtscript.util.DebugUtil;
import com.dtstack.engine.dtscript.util.KrbUtils;
import com.dtstack.engine.dtscript.util.Utilities;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
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
import java.util.Iterator;
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
        LOG.info("java.home :" + conf.get("java.home"));

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
        List<String> envList = new ArrayList<>(40);

        // 拉起py4j gateway server进程
        Process gatewayProcess = null;
        boolean hasKrb = KrbUtils.hasKrb(envs);
        boolean isPythonType = KrbUtils.isPythonType(appType.name());
        if (hasKrb && isPythonType) {
            int port = NetUtils.getAvailablePort();
            String[] py4jEnv = new String[2];
            py4jEnv[0] = DtYarnConstants.ENV_GATEWAY_PORT + "=" + port;
            py4jEnv[1] = DtYarnConstants.ENV_PRINCIPAL + "=" + envs.get(DtYarnConstants.ENV_PRINCIPAL);
            // TODO 一些常量下沉到base或者新建一个模块
            final String mainClass = "com.dtstack.python.PythonGatewayServer";
            final String javaHome = conf.get("java.home");
            Preconditions.checkState(javaHome != null, "JAVA_HOME没有设置请联系运维配置所有NodeManager节点的JAVA_HOME环境变量");
            String py4jStartCmd = javaHome + "/bin/java -cp " + DtYarnConstants.LOCALIZED_GATEWAY_PATH + " " + mainClass;
            gatewayProcess = Runtime.getRuntime().exec(py4jStartCmd, py4jEnv);
            // FIXME 未来应该取消掉sleep 目前是假定sleep后gateway server已经启动好，再拉起Python进程。
            Thread.sleep(2400);
            envList.add(py4jEnv[0]);
        }

        // set current process envs to subProcess envs
        Iterator it = envs.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (!StringUtils.startsWith(key,AppEnvConstant.SUB_PROCESS_ENV)) {
                continue;
            }
            envList.add(StringUtils.replace(key, AppEnvConstant.SUB_PROCESS_ENV, "") + "=" + value);
        }

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

        for (String str : env) {
            LOG.info("Python Process Env : " + str);
        }

        Process process = Runtime.getRuntime().exec(cmd, env);

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
        if (hasKrb && isPythonType && gatewayProcess != null) {
            gatewayProcess.destroy();
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

    private String readFile(String filePath) throws IOException {

        LOG.info("start read file");
        StringBuffer sb = new StringBuffer();
        String line;
        try (
                InputStream is = new FileInputStream(filePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, DtYarnConfiguration.UTF8));
            ) {
            line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOG.error("Read file error: ", e);
            throw new RuntimeException(e);
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