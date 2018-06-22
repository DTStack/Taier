package com.dtstack.rdos.engine.execution.xlearning;

import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import net.qihoo.xlearning.client.Client;
import net.qihoo.xlearning.conf.XLearningConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * xlearning客户端
 * Date: 2018/6/22
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class XlearningClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(XlearningClient.class);
    private XLearningConfiguration conf = new XLearningConfiguration();

    @Override
    public void init(Properties prop) throws Exception {
        Enumeration enumeration =  prop.propertyNames();
        while(enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            conf.set(key, (String) prop.get(key));
        }
        //client = new Client(args);
    }

    @Override
    public JobResult cancelJob(String jobId) {
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        return null;
    }

    @Override
    public String getJobMaster() {
        return null;
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    @Override
    public JobResult submitPythonJob(JobClient jobClient){
        return null;
    }


}
