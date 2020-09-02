package com.dtstack.engine.master.env;

import com.dtstack.engine.common.util.AddressUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * @author sishu.yss
 */
@Component
@PropertySource(value = "file:${user.dir}/conf/application.properties")
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
        return Math.abs(Integer.parseInt(environment.getProperty("cycTimeDayGap", "0")));
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
        return Integer.parseInt(environment.getProperty("max.pool.size", "1000"));
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

    public String getHttpAddress() {
        return environment.getProperty("http.address", "0.0.0.0");
    }

    /**
     * =========redis=======
     */

    public int getRedisDB() {
        return Integer.parseInt(environment.getProperty("redis.db", "1"));
    }

    public int getCacheActiveCount() {
        return Integer.parseInt(environment.getProperty("ehredis.active.count", "10"));
    }

    public boolean getCacheActiveRedis() {
        return Boolean.parseBoolean(environment.getProperty("ehredis.active.redis", "true"));
    }

    public int getCacheLiveTime() {
        return Integer.parseInt(environment.getProperty("ehredis.live.time", "3600"));
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

    /**
     *  ===es====
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

    public String getHdfsBatchPath() {
        return environment.getProperty("hdfs.batch.path", "/rdos/batch/");
    }

    /**
     * =======vertx======
     */

    public int getInstances() {
        return Integer.parseInt(environment.getProperty("vertx.instance", String.valueOf(2 * Runtime.getRuntime().availableProcessors())));
    }

    public int getEventLoopPoolSize() {
        return Integer.parseInt(environment.getProperty("event.pool.size", String.valueOf(2 * Runtime.getRuntime().availableProcessors())));
    }

    public int getWorkerPoolSize() {
        return Integer.parseInt(environment.getProperty("worker.pool.size", "1000"));
    }

    public long getMaxWorkerExecuteTime() {
        return Long.parseLong(environment.getProperty("max.worker.execute.time", String.valueOf(1 * 60 * 1000 * 1000000L)));
    }

    public String getJobGraphBuildCron() {
        return environment.getProperty("batch.job.graph.build.cron", "22:00:00");
    }

    public String getMasterLock() {
        return environment.getProperty("master.lock", "master_lock");
    }

    public String getHdfsTaskPath() {
        return environment.getProperty("hdfs.task.path", "/dtInsight/task/");
    }

    /**
     * ====engine=======
     */
    public int getSlots() {
        return Integer.parseInt(environment.getProperty("slots", "10"));
    }

    public String getLocalAddress() {
        String address = environment.getProperty("http.address", AddressUtil.getOneIp());
        String port = environment.getProperty("http.port", "8090");
        return String.format("%s:%s", address, port);
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
        return Integer.parseInt(environment.getProperty("jobStoppedRetry", "1"));
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
        return environment.getProperty("local.kerberos.dir", System.getProperty("user.dir") + "/kerberosConfig");
    }

    public String getKerberosTemplatepath() {
        return environment.getProperty("kerberos.template.path", System.getProperty("user.dir") + "/conf/kerberos");
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


    public int getBuildJobErrorRetry(){
        return Integer.parseInt(environment.getProperty("build.job.retry", "3"));
    }

    /**
     * 日志数据定时删除
     */
    public Integer getHourMax() {
        return Integer.valueOf(environment.getProperty("hourMax", "15"));
    }

    public Integer getDayMax() {
        return Integer.valueOf(environment.getProperty("dayMax", "30"));
    }

    public Integer getMonthMax() {
        return Integer.valueOf(environment.getProperty("monthMax", "60"));
    }

    public String getScheduleJobCron() {
        return environment.getProperty("job.back.cron", "23:00:00");
    }

    public boolean openScheduleJobCron(){
        return Boolean.parseBoolean(environment.getProperty("job.back.cron.open", "false"));
    }

    public long getConsoleStopExpireTime() {
        return Long.parseLong(environment.getProperty("consoleStopExpireTime", Long.toString(60 * 1000 * 24)));
    }

    public Integer getScheduleJobScope() {
        return Integer.valueOf(environment.getProperty("job.back.scope", "1000*60"));
    }

    public Integer getJobExecutorPoolCorePoolSize(){
        return Integer.valueOf(environment.getProperty("job.executor.pool.core.size", "10"));
    }

    public Integer getJobExecutorPoolMaximumPoolSize(){
        return Integer.valueOf(environment.getProperty("job.executor.pool.maximum.size", "10"));
    }

    public Long getJobExecutorPoolKeepAliveTime(){
        return Long.valueOf(environment.getProperty("job.executor.pool.keep.alive.time", "1000"));
    }

    public Integer getJobExecutorPoolQueueSize(){
        return Integer.valueOf(environment.getProperty("job.executor.pool.queue.size", "1000"));
    }
}
