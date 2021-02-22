package com.dtstack.engine.nfs;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.pojo.JobResult;
import com.emc.ecs.nfsclient.nfs.NfsSetAttributes;
import com.emc.ecs.nfsclient.nfs.nfs3.Nfs3;
import com.emc.ecs.nfsclient.rpc.CredentialUnix;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yuebai
 * @date 2020-11-10
 */
public class NfsClient extends AbstractClient {

    private static long mode = 510L;

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return null;
    }

    @Override
    public void init(Properties prop) throws Exception {

    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        return null;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        JSONObject pluginConf = JSONObject.parseObject(pluginInfo);
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            String path = pluginConf.getString("path");
            String server = pluginConf.getString("server");
            NfsClient.createClient(path,server);
            componentTestResult.setResult(true);
        } catch (Exception e) {
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            return componentTestResult;
        }
        return componentTestResult;

    }

    public static Nfs3 createClient(String path, String nfsIp) throws Exception {
        NfsSetAttributes nfsSetAttr = new NfsSetAttributes();
        nfsSetAttr.setMode(mode);
        return new Nfs3(nfsIp, path, new CredentialUnix(-2, -2, null), 3);
    }
}
