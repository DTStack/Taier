package com.dtstack.engine.rdbs.inceptor;

import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

/**
 * @program: engine-all
 * @author: wuren
 * @create: 2021/04/06
 */
public class InceptorClient extends AbstractRdbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(InceptorClient.class);

    public InceptorClient() {
        this.dbType = "inceptor";
    }

    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "admin");

        // input params json file path
        String filePath = args[0];
        File paramsFile = new File(filePath);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(new FileInputStream(paramsFile)));
        String request = reader.readLine();
        Map params = PublicUtil.jsonStrToObject(request, Map.class);
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        JobClient jobClient = new JobClient(paramAction);

        String pluginInfo = jobClient.getPluginInfo();
        Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
        String md5plugin = MD5Util.getMd5String(pluginInfo);
        properties.setProperty("md5sum", md5plugin);

        InceptorClient client = new InceptorClient();
        client.init(properties);

        JobResult result = client.submitJob(jobClient);
        LOG.info(result.toString());
        LOG.info("submit success!");
        Thread.sleep(40000);
        System.exit(0);
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new InceptorConnFactory();
    }
}
