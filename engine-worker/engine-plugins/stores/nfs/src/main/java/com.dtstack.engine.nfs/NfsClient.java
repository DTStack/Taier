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
import com.emc.ecs.nfsclient.nfs.io.Nfs3File;
import com.emc.ecs.nfsclient.nfs.nfs3.Nfs3;
import com.emc.ecs.nfsclient.rpc.CredentialUnix;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yuebai
 * @date 2020-11-10
 */
public class NfsClient extends AbstractClient {

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
            Nfs3 client = NfsClient.createClient(pluginConf.getString("path"), pluginConf.getString("server"));
            Nfs3File filePath = new Nfs3File(client, pluginConf.getString("path"));
            componentTestResult.setResult(filePath.exists());
        } catch (Exception e) {
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            return componentTestResult;
        }
        return componentTestResult;

    }

    public static Nfs3 createClient(String path, String nfsIp) throws Exception {
        NfsSetAttributes nfsSetAttr = new NfsSetAttributes();
        nfsSetAttr.setMode((long) (0x00100 + 0x00080 + 0x00040 + 0x00020 + 0x00010 + 0x00008 + 0x00004 + 0x00002));
        return new Nfs3(nfsIp, path, new CredentialUnix(-2, -2, null), 3);
    }
}
