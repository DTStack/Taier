package com.dtstack.engine.local.test;

import com.dtstack.engine.pluginapi.pojo.ParamAction;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.JobIdentifier;
import com.dtstack.engine.pluginapi.callback.CallBack;
import com.dtstack.engine.pluginapi.callback.ClassLoaderCallBackMethod;
import com.dtstack.engine.pluginapi.client.IClient;
import com.dtstack.engine.pluginapi.pojo.JobResult;
import com.dtstack.engine.pluginapi.util.MD5Util;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String SP = File.separator;

    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "admin");

        // job json path
        String jobJsonPath = USER_DIR + SP + "local-test/src/main/json/dtscript-agent.json";

        // create jobClient
        String content = getJobContent(jobJsonPath);
        Map params =  PublicUtil.jsonStrToObject(content, Map.class);
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        JobClient jobClient = new JobClient(paramAction);

        // create jobIdentifier
        String jobId = "jobId";
        String appId = "appId";
        String taskId = "taskId";
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

        // get pluginInfo
        String pluginInfo = jobClient.getPluginInfo();
        Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
        String md5plugin = MD5Util.getMd5String(pluginInfo);
        properties.setProperty("md5sum", md5plugin);

        // create client
        String pluginParentPath = USER_DIR + SP + "pluginLibs";
        IClient client = ClientFactory.buildPluginClient(pluginInfo, pluginParentPath);

        // client init
        ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {
            @Override
            public String execute() throws Exception {
                client.init(properties);
                return null;
            }
        }, client.getClass().getClassLoader(), true);

        // test target method
        ClassLoaderCallBackMethod.callbackAndReset(new CallBack<Object>() {
            @Override
            public Object execute() throws Exception {
                JobResult jobResult = client.submitJob(jobClient);
                return jobResult;
            }
        }, client.getClass().getClassLoader(), true);

        LOG.info("Launcher Success!");
        System.exit(0);
    }

    public static String getJobContent(String jobJsonPath) {
        File jobFile = new File(jobJsonPath);
        try (
                InputStreamReader isr = new InputStreamReader(new FileInputStream(jobFile));
                BufferedReader reader = new BufferedReader(isr);
        ) {
            String content = "";
            String line = reader.readLine();
            while (line != null) {
                content = content + line;
                line = reader.readLine();
            }
            return content;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
