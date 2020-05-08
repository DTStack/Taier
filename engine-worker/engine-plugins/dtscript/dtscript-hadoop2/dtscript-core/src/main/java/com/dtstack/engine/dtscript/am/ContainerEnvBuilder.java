package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Build container env
 *
 * @author huyifan.zju@163.com
 */
public class ContainerEnvBuilder {

    private static final Log LOG = LogFactory.getLog(ContainerEnvBuilder.class);

    private String role;

    private ApplicationMaster applicationMaster;

    public ContainerEnvBuilder(String role, ApplicationMaster applicationMaster) {
        this.role = role;
        this.applicationMaster = applicationMaster;
    }

    public Map<String,String> build() {
        final String learningAppType = applicationMaster.appArguments.learningAppType;
        final Configuration conf = applicationMaster.conf;
        final String cmd = applicationMaster.appArguments.cmd;
        final int workerNum = applicationMaster.appArguments.workerNum;
        final ApplicationAttemptId applicationAttemptId = applicationMaster.appArguments.applicationAttemptID;
        final ApplicationContainerListener containerListener = applicationMaster.containerListener;

        LOG.info("Setting environments for the Container");
        Map<String, String> containerEnv = new HashMap<>();

        containerEnv.put(DtYarnConstants.Environment.HADOOP_USER_NAME.toString(), conf.get("hadoop.job.ugi").split(",")[0]);
        containerEnv.put(DtYarnConstants.Environment.DT_EXEC_CMD.toString(), cmd);
        containerEnv.put("CLASSPATH", System.getenv("CLASSPATH"));
        containerEnv.put(DtYarnConstants.Environment.APP_ATTEMPTID.toString(), applicationAttemptId.toString());
        containerEnv.put(DtYarnConstants.Environment.APP_ID.toString(), applicationAttemptId.getApplicationId().toString());
        containerEnv.put(DtYarnConstants.Environment.APPMASTER_HOST.toString(),
                System.getenv(ApplicationConstants.Environment.NM_HOST.toString()));
        containerEnv.put(DtYarnConstants.Environment.APPMASTER_PORT.toString(),
                String.valueOf(containerListener.getServerPort()));

        StringBuilder pathStr = new StringBuilder();
        pathStr.append(System.getenv("PATH")).append(":");
        if (StringUtils.isNotBlank(System.getenv(DtYarnConstants.Environment.USER_PATH.toString()))) {
            pathStr.append(System.getenv(DtYarnConstants.Environment.USER_PATH.toString())).append(":");
        }
        if (StringUtils.isNotBlank(conf.get(DtYarnConstants.Environment.USER_PATH.toString()))) {
            pathStr.append(conf.get(DtYarnConstants.Environment.USER_PATH.toString()));
        }
        containerEnv.put("PATH", pathStr.toString());

        containerEnv.put(DtYarnConstants.Environment.APP_TYPE.toString(),learningAppType);
        SecurityUtil.setupUserEnv(containerEnv);

        LOG.info("container env:" + containerEnv.toString());
        Set<String> envStr = containerEnv.keySet();
        for (String anEnvStr : envStr) {
            LOG.debug("env:" + anEnvStr);
        }

        return containerEnv;

    }
}
