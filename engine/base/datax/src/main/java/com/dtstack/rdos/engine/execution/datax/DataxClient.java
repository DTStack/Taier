package com.dtstack.rdos.engine.execution.datax;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.dtstack.rdos.common.ssh.SSHClient;
import com.dtstack.rdos.common.util.LocalIpAddressUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.google.common.collect.Lists;

/**
 * @author sishu.yss
 */
public class DataxClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(DataxClient.class);

    private static final String DATAX_USERNAME_KEY = "userName";
    private static final String DATAX_PASSWORD_KEY = "password";
    private static final String DATAX_ADDRESS_KEY = "dataxSSHAddress";
    private static final String DATAX_WEB_ADDRESS_KEY = "rdosWebAddress";
    private static final String DATAX_BIN_DIR_KEY = "dataxBinDir";

    private static final List<String> dataxAddresses = Lists.newArrayList();

    private static final String DATAX_WEB_URL_TEMPLATE = "http://%s/api/batch/batchTask/%s";
    private static final String DATAX_JOB_COMMAND_TEMPLATE = "cd %s && nohup python dataxnew.py %s --rip %s --jobid %s &";

    private String DATAX_WEB_ADDRESS = null;
    private String DATAX_BIN_DIR = null;
    private String USERNAME = null;
    private String PASSWORD = null;

    private final static Random random = new Random();

    @Override
    public void init(Properties prop) {
        String userName = prop.getProperty(DATAX_USERNAME_KEY);
        Preconditions.checkArgument(StringUtils.isNoneBlank(userName), "please set username for nodeyml.xml..");
        PASSWORD = userName;

        String password = prop.getProperty(DATAX_PASSWORD_KEY);
        Preconditions.checkArgument(StringUtils.isNoneBlank(password), "please set password for nodeyml.xml.");
        PASSWORD = password;

        String address = prop.getProperty(DATAX_ADDRESS_KEY);
        dataxAddresses.addAll(Arrays.asList(address.split(",")));
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(dataxAddresses), "please set address for nodeyml.xml, more than two element split for (,).");

        DATAX_WEB_ADDRESS = (String) prop.get(DATAX_WEB_ADDRESS_KEY);
        DATAX_BIN_DIR = (String) prop.get(DATAX_BIN_DIR_KEY);
    }

    private String randomAddress() {
        return dataxAddresses.get(random.nextInt(dataxAddresses.size()));
    }

    @Override
    public JobResult cancelJob(String jobId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getJobDetail(String jobId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JobResult immediatelySubmitJob(JobClient jobClient) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JobResult submitSyncJob(JobClient jobClient) {

        String jobUrl = String.format(DATAX_WEB_URL_TEMPLATE, DATAX_WEB_ADDRESS, jobClient.getTaskId());
        String ip = LocalIpAddressUtil.getLocalAddress();

        //todo
        Long jobId = 1L;
        String command = String.format(DATAX_JOB_COMMAND_TEMPLATE, DATAX_BIN_DIR, jobUrl, DATAX_WEB_ADDRESS, jobId);

        JobResult jobResult = null;
        try {
            SSHClient sshClient = new SSHClient(USERNAME, PASSWORD, randomAddress());
            String result = sshClient.ssh(command);
            jobResult = JobResult.createSuccessResult(jobId.toString());
        } catch (Exception e) {
            jobResult = JobResult.createErrorResult(e);
            logger.error("", e);
        }

        return jobResult;
    }

}
