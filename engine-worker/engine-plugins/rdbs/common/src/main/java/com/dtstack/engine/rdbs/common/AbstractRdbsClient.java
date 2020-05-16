package com.dtstack.engine.rdbs.common;

import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.client.config.YamlConfigParser;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.ComponentTestResult;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.rdbs.common.constant.ConfigConstant;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import com.dtstack.engine.rdbs.common.executor.RdbsExeQueue;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 和其他类型的client不同--需要等待sql执行完成。
 * Date: 2018/2/27
 * Company: www.dtstack.com
 * @author jingzhen
 */

public abstract class AbstractRdbsClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRdbsClient.class);

    private RdbsExeQueue exeQueue;

    private EngineResourceInfo resourceInfo;

    private AbstractConnFactory connFactory;

    protected String dbType = "rdbs";

    protected abstract AbstractConnFactory getConnFactory();

    public AbstractRdbsClient() {
        try {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(PLUGIN_DEFAULT_CONFIG_NAME);
            Map<String, Object> config = YamlConfigParser.INSTANCE.parse(resourceAsStream);
            defaultPlugins = super.convertMapTemplateToConfig(config);
            LOG.info("=======RdbClient============{}", defaultPlugins);
        } catch (Exception e) {
            LOG.error("RdbClient client init default config error {}", e);
        }
    }

    @Override
    public void init(Properties prop) throws Exception {

        connFactory = getConnFactory();
        connFactory.init(prop);

        exeQueue = new RdbsExeQueue(connFactory, MathUtil.getIntegerVal(prop.get(ConfigConstant.MAX_JOB_POOL_KEY)),
                MathUtil.getIntegerVal(prop.get(ConfigConstant.MIN_JOB_POOL_KEY)));
        exeQueue.init();
        resourceInfo = new RdbsResourceInfo(exeQueue);
        LOG.warn("-------init {} plugin success-----, properties={}", dbType, prop.toString());
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.MR.equals(jobType)){
            jobResult = submitJobWithJar(jobClient);
        }else if(EJobType.SQL.equals(jobType)){
            jobResult = submitSqlJob(jobClient);
        }
        return jobResult;
    }

    private JobResult submitSqlJob(JobClient jobClient) {
        String submitId = exeQueue.submit(jobClient);
        return JobResult.createSuccessResult(submitId);
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        throw new RdosDefineException(dbType + "client not support MR job");
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        boolean cancelResult = exeQueue.cancelJob(jobId);
        if(cancelResult){
            return JobResult.createSuccessResult(jobId);
        }

        return JobResult.createErrorResult("can't not find the job");
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        String jobId = jobIdentifier.getEngineJobId();
        return exeQueue.getJobStatus(jobId);
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        throw new RdosDefineException(dbType + " client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosDefineException(dbType + "client not support method 'getMessageByHttp'");
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        return exeQueue.getJobLog(jobId);
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return resourceInfo.judgeSlots(jobClient);
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
            if(Objects.isNull(connFactory)){
                synchronized (AbstractRdbsClient.class){
                    if(Objects.isNull(connFactory)){
                        connFactory = getConnFactory();
                    }
                }
            }
            connFactory.init(properties);
            componentTestResult.setResult(true);
        } catch (Exception e) {
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            componentTestResult.setResult(false);
        }
        return componentTestResult;
    }

    @Override
    public List<List<Object>> executeQuery(String pluginInfo, String sql, String database) {
        Statement statement = null;
        ResultSet res = null;
        Connection conn = null;
        List<List<Object>> result = Lists.newArrayList();
        try {
            if (StringUtils.isBlank(sql)) {
                return null;
            }
            Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
            if (Objects.isNull(connFactory)) {
                synchronized (AbstractRdbsClient.class) {
                    if (Objects.isNull(connFactory)) {
                        connFactory = getConnFactory();
                    }
                }
            }
            connFactory.init(properties);
            conn = connFactory.getConn();
            statement = conn.createStatement();
            if (StringUtils.isNotBlank(database)) {
                statement.execute("use " + database);
            }

            if (statement.execute(sql)) {
                res = statement.getResultSet();
                int columns = res.getMetaData().getColumnCount();
                List<Object> cloumnName = Lists.newArrayList();
                int timeStamp = 0;
                SimpleDateFormat dateFormat = null;

                for (int i = 1; i <= columns; ++i) {
                    String name = res.getMetaData().getColumnName(i);
                    if (name.contains(".")) {
                        name = name.split("\\.")[1];
                    }
                    cloumnName.add(name);
                }

                result.add(cloumnName);

                while (res.next()) {
                    List<Object> objects = Lists.newArrayList();

                    for (int i = 1; i <= columns; ++i) {
                        if (i == timeStamp && Objects.nonNull(dateFormat)) {
                            objects.add(dateFormat.format(res.getObject(i)));
                        } else {
                            objects.add(res.getObject(i));
                        }
                    }

                    result.add(objects);
                }
            }
        } catch (Exception e) {
            LOG.error("execue sql {} error",sql,e);
        } finally {
            try {
                if (res != null) {
                    res.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (null != conn){
                    conn.close();;
                }
            } catch (Throwable var18) {
                LOG.error("", var18);
            }
        }
        return result;
    }
}
