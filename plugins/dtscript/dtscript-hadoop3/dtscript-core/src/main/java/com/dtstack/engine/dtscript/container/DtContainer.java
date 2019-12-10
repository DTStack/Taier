package com.dtstack.engine.dtscript.container;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.common.DTScriptConstant;
import com.dtstack.engine.dtscript.common.type.AppType;
import com.dtstack.engine.dtscript.api.ApplicationContainerProtocol;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import com.dtstack.engine.dtscript.common.ReturnValue;
import com.dtstack.engine.dtscript.common.type.DummyType;
import com.dtstack.engine.dtscript.util.DebugUtil;
import com.dtstack.engine.dtscript.util.KerberosUtils;
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
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
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

    private FileSystem dfs;

    private final AppType appType;

    private final Map<String,Object> containerInfo;

    private DtContainer() throws IOException {
        containerInfo = new HashMap<>();

        this.conf = new DtYarnConfiguration();

        Path hdfsSidePath = new Path("hdfs-side.xml");
        Path coreSidePath = new Path("core-side.xml");

        conf.addResource(new Path(DtYarnConstants.LEARNING_JOB_CONFIGURATION));
        conf.addResource(coreSidePath);
        conf.addResource(hdfsSidePath);

        localFs = FileSystem.getLocal(conf);

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
            LOG.info("appMasterHost:" + appMasterHost + ", port:" + appMasterPort);
            final Configuration newConf = new Configuration(conf);

            if (KerberosUtils.isOpenKerberos(conf)){
                UserGroupInformation myGui = UserGroupInformation.loginUserFromKeytabAndReturnUGI(conf.get("hdfsPrincipal"),
                        KerberosUtils.downloadAndReplace(newConf,"hdfsKeytabPath"));
                UserGroupInformation.setLoginUser(myGui);
                UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
                LOG.info("-ugi---:" + ugi);
                LOG.info("isenabled:" + UserGroupInformation.isSecurityEnabled());
                LOG.info("hdfs principal:" + conf.get("hdfsPrincipal"));
                newConf.set(DTScriptConstant.RPC_SERVER_PRINCIPAL, conf.get("hdfsPrincipal"));
                newConf.set(DTScriptConstant.RPC_SERVER_KEYTAB, KerberosUtils.downloadAndReplace(newConf, "hdfsKeytabPath"));
                UserGroupInformation.setConfiguration(newConf);
                SecurityUtil.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS, newConf);

                SecurityUtil.login(newConf, DTScriptConstant.RPC_SERVER_KEYTAB, DTScriptConstant.RPC_SERVER_PRINCIPAL);
            }

            amClient = RPC.getProxy(ApplicationContainerProtocol.class, ApplicationContainerProtocol.versionID, addr, newConf);
            LocalRemotePath[] localRemotePaths = amClient.getOutputLocation("localRemotePath");
            LOG.info("get localRemotePaths:" + localRemotePaths.length);

            this.dfs = FileSystem.get(conf);
        } catch (Exception e) {
            LOG.error("-----------", e);
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
        int exitValue = process.exitValue();
        LOG.info("container_wait_for_end exitValue: " + exitValue);
        String log = readFile(logDir + "/dterror.log");

        ReturnValue rValue = new ReturnValue(exitValue, log);
        return rValue;

    }

    private void printInfo(InputStream inputStream){

        InputStreamReader isr = new InputStreamReader(inputStream);
        //用缓冲器读行
        BufferedReader br = new BufferedReader(isr);
        String line;
        //直到读完为止

        try{
            while((line = br.readLine()) != null) {
                LOG.info(line);
            }

        }catch (Exception e){
            LOG.warn("exception:", e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOG.warn("exception:", e);
            }
        }
    }

    private String getFirstLogFile(String logFile){

        if(Strings.isNullOrEmpty(logFile)){
            return logFile;
        }

        String[] splitStr = logFile.split(",");
        if(splitStr.length == 0){
            return logFile;
        }

        return splitStr[0];
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

        System.exit(1);
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
        List<LocalRemotePath> outputs = Arrays.asList(amClient.getOutputLocation("outputs"));
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
        LOG.info("uploadOutputFiles end");
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

    public static String readFile(String filePath) throws IOException {

        LOG.info("start read file");
        StringBuffer sb = new StringBuffer();
        InputStream is = new FileInputStream(filePath);
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = reader.readLine();
        }
        reader.close();
        is.close();
        LOG.info("end read file");
        return sb.toString();
    }

    public static void main(String[] args) {
        DtContainer container = null;
        try {
            container = new DtContainer();
            container.init();
            ReturnValue response = container.run();
            if (response.getExitValue() == 0) {
                LOG.info("DtContainer " + container.getContainerId().toString() + " finish successfully");
                container.reportSucceededAndExit();
            } else if (response.getExitValue() == ContainerExecutor.ExitCode.FORCE_KILLED.getExitCode()
                    || response.getExitValue() == ContainerExecutor.ExitCode.TERMINATED.getExitCode()) {
                LOG.warn("DtContainer run exited!");
                container.reportFailedAndExit(response.getErrorLog());
            } else {
                LOG.error("DtContainer run failed! error");
                container.reportFailedAndExit(response.getErrorLog());
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