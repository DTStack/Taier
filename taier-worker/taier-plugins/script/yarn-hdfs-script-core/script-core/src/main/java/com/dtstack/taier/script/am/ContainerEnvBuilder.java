package com.dtstack.taier.script.am;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.script.ScriptConfiguration;
import com.dtstack.taier.script.api.ScriptConstants;
import com.dtstack.taier.script.common.AppEnvConstant;
import com.dtstack.taier.script.common.SecurityUtil;
import com.dtstack.taier.script.util.Utilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Build container env
 *
 * @author huyifan.zju@163.com
 */
public class ContainerEnvBuilder {

    private static final Log LOG = LogFactory.getLog(ContainerEnvBuilder.class);

    private String role;

    private ApplicationMaster applicationMaster;

    private Map<String, String> envs;

    /**
     * env parameters of notebook job
     */
    private static final String MODEL_ENV = "modelEnv";
    /**
     * reg pattern of key-value parse
     */
    private static final Pattern p = Pattern.compile("([\\w.]+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");

    public ContainerEnvBuilder(String role, ApplicationMaster applicationMaster) {
        this.role = role;
        this.applicationMaster = applicationMaster;
    }

    public Map<String, String> build(ScriptConfiguration dtconf) {
        envs = System.getenv();
        final Configuration conf = applicationMaster.yarnconf;
        final String appEnv = envs.get(ScriptConstants.Environment.APP_ENV.toString());
        ApplicationAttemptId applicationAttemptId;
        if (envs.containsKey(ApplicationConstants.Environment.CONTAINER_ID.toString())) {
            ContainerId containerId = ConverterUtils
                    .toContainerId(envs.get(ApplicationConstants.Environment.CONTAINER_ID.toString()));
            LOG.info("container_id: " + containerId.toString());
            applicationAttemptId = containerId.getApplicationAttemptId();
            LOG.info("second applicationAttemptID: " + applicationAttemptId);
        } else {
            throw new IllegalArgumentException(
                    "Application Attempt Id is not available in environment");
        }

        final ApplicationContainerListener containerListener = applicationMaster.containerListener;


        LOG.info("Setting environments for the Container");
        Map<String, String> containerEnv = new HashMap<>();
        containerEnv.putAll(Utilities.getEnvironmentVariables(ScriptConstants.SCRIPT_ENV_PREFIX, (YarnConfiguration) conf));

        containerEnv.put(ScriptConstants.Environment.SCRIPT_CONTAIENR_GPU_NUM.toString(),
                String.valueOf(dtconf.getInt(ScriptConfiguration.SCRIPT_WORKER_GPU, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_GPU)));
        String hadoopUserNameKey = ScriptConstants.Environment.HADOOP_USER_NAME.toString();
        containerEnv.put(hadoopUserNameKey, System.getenv(hadoopUserNameKey));
        containerEnv.put("CLASSPATH", System.getenv("CLASSPATH"));
        containerEnv.put(ScriptConstants.Environment.APP_ATTEMPTID.toString(), applicationAttemptId.toString());
        containerEnv.put(ScriptConstants.Environment.APP_ID.toString(), applicationAttemptId.getApplicationId().toString());
        containerEnv.put(ScriptConstants.Environment.APPMASTER_HOST.toString(),
                System.getenv(ApplicationConstants.Environment.NM_HOST.toString()));
        containerEnv.put(ScriptConstants.Environment.APPMASTER_PORT.toString(),
                String.valueOf(containerListener.getServerPort()));

        containerEnv.put(ScriptConstants.Environment.APP_TYPE.toString(), envs.get(ScriptConstants.Environment.APP_TYPE.toString()));
        SecurityUtil.setupUserEnv(containerEnv);

        // set cmd into container envs
        containerEnv.put(ScriptConstants.Environment.EXEC_CMD.toString(), envs.get(ScriptConstants.Environment.EXEC_CMD.toString()));
        parseAppEnv(appEnv, containerEnv);

        LOG.info("container env:" + containerEnv.toString());
        Set<String> envStr = containerEnv.keySet();
        for (String anEnvStr : envStr) {
            LOG.debug("env:" + anEnvStr);
        }

        return containerEnv;

    }


    /**
     * 1.parse cmd.
     * 2.add key-value into container envs.
     *
     * @param appEnv
     */
    private void parseAppEnv(String appEnv, Map<String, String> containerEnv) {
        if (StringUtils.isBlank(appEnv)) {
            return;
        }

        try {
            // envJson is encode, we need decode it.
            appEnv = URLDecoder.decode(appEnv, "UTF-8");
            LOG.info("cmdStr decoded is : " + appEnv);

            Map<String, Object> envMap = JSON.parseObject(appEnv.trim());
            Iterator entries = envMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key = (String) entry.getKey();
                String value;
                if (AppEnvConstant.MODEL_PARAM.equals(key)) {
                    value = URLEncoder.encode((String) entry.getValue(), "UTF-8");
                } else {
                    value = (String) entry.getValue();
                }
                //add prefix for app env, make it easier to recognize.
                containerEnv.put(AppEnvConstant.SUB_PROCESS_ENV.concat(key), value);
            }
        } catch (Exception e) {
            String message = String.format("Could't parse {%s} to json format. Reason : {%s}", appEnv, e.getMessage());
            LOG.error(message);
            throw new RuntimeException(message, e);
        }
    }
}
