package com.dtstack.yarn.container;

import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.api.ApplicationContainerProtocol;
import com.dtstack.yarn.api.DtYarnConstants;
import com.dtstack.yarn.common.DtContainerStatus;
import com.dtstack.yarn.common.LocalRemotePath;
import com.dtstack.yarn.common.type.AppType;
import com.dtstack.yarn.common.type.DummyType;
import com.dtstack.yarn.util.DebugUtil;
import com.dtstack.yarn.util.KerberosUtils;
import com.dtstack.yarn.util.Utilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.ipc.WritableRpcEngine;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.net.SocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.hadoop.ipc.RPC.getRpcTimeout;

public class DtContainer {

    private static final Log LOG = LogFactory.getLog(DtContainer.class);

    private DtYarnConfiguration conf;

    private ApplicationContainerProtocol amClient;

    private DtContainerId containerId;

    private Map<String, String> envs;

    private String role;

    private ContainerStatusNotifier containerStatusNotifier;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final FileSystem localFs;

    private final FileSystem dfs;

    private final AppType appType;

    private final Map<String,Object> containerInfo;

    private DtContainer() throws IOException {
        containerInfo = new HashMap<>();

        this.conf = new DtYarnConfiguration();

        this.dfs = FileSystem.get(conf);

        localFs = FileSystem.getLocal(conf);

        conf.addResource(new Path(DtYarnConstants.LEARNING_JOB_CONFIGURATION));
        LOG.info("user is " + conf.get("hadoop.job.ugi"));
        containerId = new DtContainerId(ConverterUtils.toContainerId(System
                .getenv(ApplicationConstants.Environment.CONTAINER_ID.name())));
        LOG.info("sub container id: " + containerId);
        this.envs = System.getenv();
        this.role = envs.get(DtYarnConstants.Environment.XLEARNING_TF_ROLE.toString());
        if (envs.containsKey(DtYarnConstants.Environment.APP_TYPE.toString())) {
            String applicationType = envs.get(DtYarnConstants.Environment.APP_TYPE.toString()).toUpperCase();
            appType = AppType.fromString(applicationType);
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
            LOG.info(UserGroupInformation.isSecurityEnabled());
            KerberosUtils.login(conf.get("hdfsPrincipal"), conf.get("hdfsKeytabPath"), conf.get("hdfsKrb5ConfPath"), conf);
            LOG.info(UserGroupInformation.getCurrentUser());
            LOG.info(UserGroupInformation.isSecurityEnabled());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            /*amClient = RPC.getProxy(ApplicationContainerProtocol.class,
                    ApplicationContainerProtocol.versionID, addr, conf);*/
            Class protocol = ApplicationContainerProtocol.class;
            UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
            SocketFactory factory = NetUtils.getDefaultSocketFactory(conf);
            amClient = (ApplicationContainerProtocol) new WritableRpcEngine().getProxy(protocol, ApplicationContainerProtocol.versionID,
                    addr, ugi, conf, factory, getRpcTimeout(conf), null).getProxy();
            LOG.info("dtc125: " + ugi);
        } catch (IOException e) {
            LOG.error("Connecting to ApplicationMaster " + appMasterHost + ":" + appMasterPort + " failed!");
            LOG.error("Container will suicide!");
            System.exit(1);
        }

        containerInfo.put("host", NetUtils.getHostname());

        containerStatusNotifier = new ContainerStatusNotifier(amClient, conf, containerId);
        containerStatusNotifier.start();
        containerStatusNotifier.reportContainerStatusNow(DtContainerStatus.INITIALIZING);

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

    private Boolean run() throws IOException, InterruptedException {

        Date now = new Date();
        containerStatusNotifier.setContainersStartTime(now.toString());
        containerStatusNotifier.reportContainerStatusNow(DtContainerStatus.RUNNING);

        List<String> envList = new ArrayList<>(20);
        appType.env(envList);

        String[] env = envList.toArray(new String[envList.size()]);
        String logDir = envs.get(ApplicationConstants.Environment.LOG_DIRS.name());
        String command = envs.get(DtYarnConstants.Environment.DT_EXEC_CMD.toString())
                + " 1>" +logDir
                + "/dtstdout.log 2>"+logDir+"/dterror.log";
        command = appType.cmdContainerExtra(command, containerInfo);

        String[] cmd = {"bash", "-c", command};

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

        LOG.info("container_wait_for_begin");

        process.waitFor();

        LOG.info("container_wait_for_end exitValue: " + process.exitValue());

        return process.exitValue() == 0;

    }


    private void reportFailedAndExit(String msg) {
        LOG.info("reportFailedAndExit: " + msg);
        if(msg == null || msg.length() == 0) {
            LOG.error("my error msg is: " + msg);
        }
        Date now = new Date();
        containerStatusNotifier.setContainerErrorMessage(msg);
        containerStatusNotifier.setContainersFinishTime(now.toString());
        containerStatusNotifier.reportContainerStatusNow(DtContainerStatus.FAILED);

        Utilities.sleep(3000);

        System.exit(-1);
    }

    private void reportSucceededAndExit() {
        LOG.info("reportSucceededAndExit");
        Date now = new Date();
        containerStatusNotifier.setContainersFinishTime(now.toString());
        containerStatusNotifier.reportContainerStatusNow(DtContainerStatus.SUCCEEDED);

        Utilities.sleep(3000);

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
                    String hostName =  (InetAddress.getLocalHost()).getHostName();
                    if (hostName == null) {
                        hostName = "";
                    }
                    LOG.info(hostName + "Upload output " + localAbsolutePath + " to remote path " + remotePath + " finished.");
                }
            }
        }
        LOG.info("uploadOutputFiles start");
    }

    private void printContainerInfo() throws IOException {
        FSDataOutputStream out = null;
        try {
            ContainerId cId = containerId.getContainerId();
            Path cIdPath = Utilities.getRemotePath(conf, cId.getApplicationAttemptId().getApplicationId(), "containers/" + cId.toString());
            if (dfs.exists(cIdPath)) {
                dfs.delete(cIdPath);
            }
            out = FileSystem.create(cIdPath.getFileSystem(conf), cIdPath, new FsPermission(FsPermission.createImmutable((short) 0777)));
            out.writeUTF(new ObjectMapper().writeValueAsString(containerInfo));
        } finally {
            IOUtils.closeStream(out);
        }
    }

    public static void main(String[] args) {
        DtContainer container = null;
        try {
            container = new DtContainer();
            container.init();
            if (container.run()) {
                LOG.info("DtContainer " + container.getContainerId().toString() + " finish successfully");
                container.reportSucceededAndExit();
            } else {
                LOG.error("DtContainer run failed! error");
                container.reportFailedAndExit("");
            }
        } catch (Throwable e) {
            LOG.error("Some errors has occurred during container running!", e);
            if (container!=null){
                container.reportFailedAndExit(DebugUtil.stackTrace(e));
            }
        }

        Utilities.sleep(3000);
    }


}