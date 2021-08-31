package com.dtstack.engine.common.env;

import com.dtstack.engine.common.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * @author sishu.yss
 */
@Component
@PropertySource(value = "file:${user.dir.conf}/application.properties")
public class EnvironmentContext {


    @Autowired
    private Environment environment;

    /**
     * =========base=======
     */
    public String getSecurity() {
        return environment.getProperty("security");
    }

    public Long getAcquireQueueJobInterval() {
        return Long.parseLong(environment.getProperty("acquireQueueJobInterval", "3000"));
    }

    public Integer getCycTimeDayGap() {
        return Math.abs(Integer.parseInt(environment.getProperty("cycTimeDayGap", "1")));
    }

    public long getJobStatusDealerInterval() {
        return Integer.parseInt(environment.getProperty("jobStatusDealerInterval", "3000"));
    }

    public String getAlarmTitle() {
        String alarmTitle = environment.getProperty("alarmTitle");
        return StringUtils.isNotBlank(alarmTitle) ? alarmTitle : "袋鼠云数栈";
    }

    public long getAlarmProcessorInterval() {
        return Integer.parseInt(environment.getProperty("alarmProcessorInterval", "60000"));
    }

    /**
     * =========jdbc=======
     */
    public String getJdbcDriverClassName() {
        return environment.getProperty("jdbc.driverClassName");
    }

    public String getJdbcUrl() {
        return environment.getProperty("jdbc.url");
    }

    public String getJdbcPassword() {
        return environment.getProperty("jdbc.password");
    }

    public String getJdbcUser() {
        return environment.getProperty("jdbc.username");
    }

    public int getMaxPoolSize() {
        return Integer.parseInt(environment.getProperty("max.pool.size", "500"));
    }

    public int getMinPoolSize() {
        return Integer.parseInt(environment.getProperty("min.pool.size", "50"));
    }

    public int getInitialPoolSize() {
        return Integer.parseInt(environment.getProperty("initial.pool.size", "50"));
    }

    public int getCheckTimeout() {
        return Integer.parseInt(environment.getProperty("check.timeout", "30000"));
    }

    public int getMaxWait() {
        return Integer.parseInt(environment.getProperty("max.wait", "10000"));
    }

    /**
     * =========mybatis=======
     */

    public String getMybatisMapperLocations() {
        return environment.getProperty("mybatis.mapper-locations", "classpath*:sqlmap/*-mapper.xml");
    }

    public String getMybatisConfigLocation() {
        return environment.getProperty("mybatis.config-location", "classpath:mybatis-config.xml");
    }

    /**
     * =========end=======
     */
    public Integer getHttpPort() {
        return Integer.parseInt(environment.getProperty("http.port", "9020"));
    }

    private volatile String httpAddress;

    public String getHttpAddress() {
        if (StringUtils.isNotBlank(httpAddress)) {
            return httpAddress;
        }

        httpAddress = environment.getProperty("http.address", AddressUtil.getOneIp());
        return httpAddress;
    }

    /**
     * =========redis=======
     */

    public int getRedisDB() {
        return Integer.parseInt(environment.getProperty("redis.db", "1"));
    }

    public String getRedisUrl() {
        return environment.getProperty("redis.url", "127.0.0.1");
    }

    public int getRedisPort() {
        return Integer.parseInt(environment.getProperty("redis.port", "6379"));
    }

    public int getMaxIdle() {
        return Integer.parseInt(environment.getProperty("redis.max.idle", "30"));
    }


    public int getRedisTimeout() {
        return Integer.parseInt(environment.getProperty("redis.timeout", "3000"));
    }


    public String getRedisPassword() {
        return environment.getProperty("redis.password");
    }

    public int getMaxTotal() {
        return Integer.parseInt(environment.getProperty("redis.max.total", "50"));
    }

    public int getMaxWaitMills() {
        return Integer.parseInt(environment.getProperty("redis.max.wait..millis", "1000"));
    }

    public int getRdosSessionExpired() {
        return Integer.parseInt(environment.getProperty("web.session.expired", "1800"));
    }

    public String getRedisSentinel() {
        return environment.getProperty("redis.sentinel", "");
    }

    public String getDtUicUrl() {
        return environment.getProperty("dtuic.url");
    }


    public String getUicToken() {
        return environment.getProperty("dtuic.token");
    }

    public boolean isOpenLdapCache(){
        return Boolean.parseBoolean(environment.getProperty("open.ldap.cache", "true"));
    }

    /**
     * ===es====
     */
    public String getElasticsearchAddress() {
        return environment.getProperty("es.address");
    }

    public String getElasticsearchUsername() {
        return environment.getProperty("es.username");
    }

    public String getElasticsearchPassword() {
        return environment.getProperty("es.password");
    }

    public String getElasticsearchIndex() {
        return environment.getProperty("es.index");
    }

    public String getElasticsearchFetchSize() {
        return environment.getProperty("es.fetchSize", "500");
    }

    public String getElasticsearchKeepAlive() {
        //  默认10分钟
        return environment.getProperty("es.scroll.keepAlive", "10");
    }


    /**
     * ===============hadoop ===============
     */

    public String getHadoopUserName() {
        return environment.getProperty("hadoop.user.name", "admin");
    }


    public String getJobGraphBuildCron() {
        return environment.getProperty("batch.job.graph.build.cron", "22:00:00");
    }

    public String getHdfsTaskPath() {
        return environment.getProperty("hdfs.task.path", "/dtInsight/task/");
    }

    private volatile String localAddress;

    public String getLocalAddress() {
        if (StringUtils.isNotBlank(localAddress)) {
            return localAddress;
        }

        String address = environment.getProperty("http.address", AddressUtil.getOneIp());
        String port = environment.getProperty("http.port", "8090");
        localAddress = String.format("%s:%s", address, port);
        return localAddress;
    }

    public String getNodeZkAddress() {
        return environment.getProperty("nodeZkAddress");
    }

    public int getExeQueueSize() {
        return Integer.parseInt(environment.getProperty("exeQueueSize", "1"));
    }

    public boolean isDebug() {
        return Boolean.parseBoolean(environment.getProperty("isDebug", "false"));
    }

    public int getQueueSize() {
        return Integer.parseInt(environment.getProperty("queueSize", "500"));
    }

    public int getJobStoppedRetry() {
        return Integer.parseInt(environment.getProperty("jobStoppedRetry", "6"));
    }

    public long getJobStoppedDelay() {
        return Integer.parseInt(environment.getProperty("jobStoppedDelay", "3000"));
    }

    /**
     * plain 1:cluster、2:cluster+queue
     *
     * @return
     */
    public String getComputeResourcePlain() {
        return environment.getProperty("computeResourcePlain", "EngineTypeClusterQueueComputeType");
    }

    public long getJobRestartDelay() {
        return Long.parseLong(environment.getProperty("jobRestartDelay", Integer.toString(2 * 60 * 1000)));
    }

    public long getJobLackingDelay() {
        return Long.parseLong(environment.getProperty("jobLackingDelay", Integer.toString(2 * 60 * 1000)));
    }

    public long getJobPriorityStep() {
        return Long.parseLong(environment.getProperty("jobPriorityStep", "10000"));
    }

    public long getJobLackingInterval() {
        String intervalObj = environment.getProperty("jobLackingInterval");
        if (StringUtils.isBlank(intervalObj)) {
            long interval = getJobLackingDelay() / getQueueSize();
            long defaultInterval = 3000L;
            if (interval < defaultInterval) {
                interval = defaultInterval;
            }
            return interval;
        }
        return Long.parseLong(intervalObj);
    }

    public int getJobLackingCountLimited() {
        return Integer.parseInt(environment.getProperty("jobLackingCountLimited", "3"));
    }

    public long getJobSubmitExpired() {
        return Long.parseLong(environment.getProperty("jobSubmitExpired", "0"));
    }

    public String getHadoopConfigField() {
        return environment.getProperty("hadoop.config.field", "confHdfsPath");
    }

    public String getHadoopConfigHdfsPath() {
        return environment.getProperty("hadoop.config.hdfsPath", "/dtInsight/console/hadoop_config");
    }

    public String getLocalKerberosDir() {
        return environment.getProperty("local.kerberos.dir", System.getProperty("user.dir") + "/kerberosUploadTempDir");
    }

    public String getConfigPath() {
        return environment.getProperty("config.dir", System.getProperty("user.dir") + "/conf/");
    }

    public long getWorkerNodeTimeout() {
        return Long.parseLong(environment.getProperty("workerNodeTimeout", "10000"));
    }

    public long getJobLogDelay() {
        return Integer.parseInt(environment.getProperty("jobLogDelay", "30000"));
    }

    public boolean getCheckJobMaxPriorityStrategy() {
        return Boolean.parseBoolean(environment.getProperty("checkJobMaxPriorityStrategy", "false"));
    }

    public int getTaskStatusDealerPoolSize() {
        return Integer.parseInt(environment.getProperty("taskStatusDealerPoolSize", "10"));
    }

    public int getLogTimeout() {
        return Integer.parseInt(environment.getProperty("logTimeout", "10"));
    }

    public int getTestConnectTimeout() {
        return Integer.parseInt(environment.getProperty("testConnectTimeout", "100"));
    }

    public int getBuildJobErrorRetry() {
        return Integer.parseInt(environment.getProperty("build.job.retry", "3"));
    }

    public int getJobSubmitConcurrent() {
        return Integer.parseInt(environment.getProperty("job.submit.concurrent", "1"));
    }

    public boolean getJobGraphBuilderSwitch() {
        return Boolean.parseBoolean(environment.getProperty("jobGraphBuilderSwitch", "false"));
    }

    public boolean getJobGraphWhiteList() {
        return Boolean.parseBoolean(environment.getProperty("jobGraphWhiteList", "false"));
    }


    public boolean openDataClear() {
        return Boolean.parseBoolean(environment.getProperty("data.clear", "true"));
    }

    public Integer getJobExecutorPoolCorePoolSize() {
        return Integer.valueOf(environment.getProperty("job.executor.pool.core.size", "10"));
    }

    public Integer getJobExecutorPoolMaximumPoolSize() {
        return Integer.valueOf(environment.getProperty("job.executor.pool.maximum.size", "10"));
    }

    public Long getJobExecutorPoolKeepAliveTime() {
        return Long.valueOf(environment.getProperty("job.executor.pool.keep.alive.time", "1000"));
    }

    public Integer getJobExecutorPoolQueueSize() {
        return Integer.valueOf(environment.getProperty("job.executor.pool.queue.size", "1000"));
    }

    public Boolean getOpenConsoleSftp() {
        return Boolean.parseBoolean(environment.getProperty("console.sftp.open", "true"));
    }

    public Integer getRetryFrequency() {
        return Integer.valueOf(environment.getProperty("retry.frequency", "3"));
    }

    public Integer getRetryInterval() {
        return Integer.valueOf(environment.getProperty("retry.interval", "30000"));
    }

    public long getJobStatusCheckInterVal() {
        return Long.parseLong(environment.getProperty("job.status.check.interval", "3500"));
    }

    public String getComponentJdbcToReplace() {
        return environment.getProperty("component.jdbc.replace", "/default");
    }

    public Integer getMaxBatchTask() {
        return Integer.parseInt(environment.getProperty("max.batch.task", "100"));
    }

    public Integer getMaxBatchTaskInsert() {
        return Integer.parseInt(environment.getProperty("max.batch.task.insert", "50"));
    }

    public Integer getMaxBatchTaskSplInsert() {
        return Integer.parseInt(environment.getProperty("max.batch.task.sql.insert", "10"));
    }

    public Integer getMinEvictableIdleTimeMillis() {
        return Integer.valueOf(environment.getProperty("dataSource.min.evictable.idle.time.millis", "300000"));
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return Integer.valueOf(environment.getProperty("dataSource.time.between.eviction.runs.millis", "60000"));
    }

    /**控制任务展开层数**/
    public Integer getJobJobLevel(){
        return Integer.valueOf(environment.getProperty("max.jobJob.level","20"));
    }

    /**控制工作流节点展开层数**/
    public Integer getWorkFlowLevel(){
        return Integer.valueOf(environment.getProperty("max.workFlow.level","20"));
    }

    public Boolean getUseOptimize(){

        return Boolean.parseBoolean(environment.getProperty("engine.useOptimize","true"));
    }

    public int getMaxDeepShow() {
        return Integer.parseInt(environment.getProperty("max.deep.show", "20"));
    }

    /**
     * 是否开启任务调度
     *
     * @return
     */
    public boolean openJobSchedule() {
        return Boolean.parseBoolean(environment.getProperty("job.schedule", "true"));
    }

    public boolean getKeepAlive() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.keep.alive", "true"));
    }

    public boolean getRemoveAbandoned() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.remove.abandoned", "true"));
    }

    public Integer getRemoveAbandonedTimeout() {
        return Integer.valueOf(environment.getProperty("dataSource.remove.abandoned.timeout", "120"));
    }


    public boolean getTestWhileIdle() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.test.while.idle", "true"));
    }

    public boolean getTestOnBorrow() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.test.on.borrow", "true"));
    }

    public boolean getTestOnReturn() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.test.on.return", "true"));
    }

    public boolean getPoolPreparedStatements() {
        return Boolean.parseBoolean(environment.getProperty("dataSource.pool.prepared.statements", "true"));
    }

    public Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return Integer.valueOf(environment.getProperty("dataSource.max.prepared.statement.per.connection.size", "20"));
    }

    public long getForkJoinResultTimeOut() {
        return Long.parseLong(environment.getProperty("fork.join.timeout", Long.toString(60L * 5)));
    }
    /**
     * 是否根据版本加载默认的配置
     *
     * @return
     */
    public boolean isCanAddExtraConfig() {
        return Boolean.parseBoolean(environment.getProperty("console.extra.config", "true"));
    }


    public Integer getFuzzyProjectByProjectAliasLimit() {
        return Integer.parseInt(environment.getProperty("fuzzy.project.alias.limit", "20"));
    }

    public Long getTaskRuleTimeout() {
        return Long.parseLong(environment.getProperty("task.rule.timeout", "600000"));
    }

    public Integer getListChildTaskLimit() {
        return Integer.parseInt(environment.getProperty("list.child.task.limit", "20"));
    }

    public boolean getOpenDummy() {
        return Boolean.parseBoolean(environment.getProperty("open.dummy", "false"));
    }

    public String getPluginPath() {
        return environment.getProperty("plugin.path",  System.getProperty("user.dir") + File.separator +"pluginLibs");
    }

    public int getMaxTenantSize() {
        return Integer.parseInt(environment.getProperty("max.tenant.size", "20"));
    }

    /**
     * 数据源中心配置地址
     * @return
     */
    public String getDatasourceNode() {
        return environment.getProperty("datasource.node", "");
    }

    /**
     * SDK TOKEN
     * @return
     */
    public String getSdkToken() {
        return environment.getProperty("sdk.token", "");
    }

    public String getSqlParserDir(){
        return environment.getProperty("sqlParser.dir","/opt/dtstack/DTPlugin/SqlParser");
    }

    /**
     * 是否优先走standalone的组件
     *
     * @return
     */
    public boolean checkStandalone() {
        return Boolean.parseBoolean(environment.getProperty("check.standalone", "true"));
    }

    public Boolean getOpenErrorTop() {
        return Boolean.parseBoolean(environment.getProperty("open.error.top", "true"));
    }

    public int getBatchJobInsertSize() {
        return Integer.parseInt(environment.getProperty("batchJob.insert.size", "20"));
    }

    public int getBatchJobJobInsertSize() {
        return Integer.parseInt(environment.getProperty("batchJobJob.insert.size", "1000"));
    }
}
