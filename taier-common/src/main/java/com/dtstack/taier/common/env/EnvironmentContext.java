/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.env;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.taier.common.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * @author sishu.yss
 */
@Component("environmentContext")
@PropertySource(value = "file:${user.dir.conf}/application.properties")
public class EnvironmentContext implements InitializingBean {


    @Autowired
    private Environment environment;

    @Value("${jdbc.driverClassName:com.mysql.jdbc.Driver}")
    private String jdbcDriverClassName;

    @Value("${jdbc.url:}")
    private String jdbcUrl;

    @Value("${jdbc.password:}")
    private String jdbcPassword;

    @Value("${jdbc.username:}")
    private String jdbcUser;

    @Value("${max.pool.size:100}")
    private int maxPoolSize;

    @Value("${min.pool.size:20}")
    private int minPoolSize;

    @Value("${initial.pool.size:20}")
    private int initialPoolSize;

    @Value("${hadoop.user.name:admin}")
    private String hadoopUserName;

    @Value("${hdfs.task.path:/dtInsight/taier/}")
    private String hdfsTaskPath;

    @Value("${nodeZkAddress:}")
    private String nodeZkAddress;

    @Value("${job.graph.build.cron:22:00:00}")
    private String jobGraphBuildCron;

    @Value("${job.acquire.queue.job.interval:3000}")
    private int jobAcquireQueueJobInterval;

    @Value("${job.cyc.time.gap:2}")
    private int jobCycTimeGap;

    @Value("${job.queue.size:500}")
    private int queueSize;

    @Value("${job.stop.delay:3000}")
    private long jobStoppedDelay;

    @Value("${job.stop.retry:6}")
    private int jobStoppedRetry;

    @Value("${job.restart.delay:120000}")
    private long jobRestartDelay;

    @Value("${job.lacking.delay:120000}")
    private long jobLackingDelay;

    @Value("${job.priority.step:3}")
    private long jobPriorityStep;

    @Value("${job.lacking.count.limited:3}")
    private int jobLackingCountLimited;

    @Value("${job.submit.expired:0}")
    private long jobSubmitExpired;

    @Value("${job.log.delay:5}")
    private long jobLogDelay;

    @Value("${job.compute.resource.plain:EngineTypeClusterQueueComputeType}")
    private String computeResourcePlain;

    @Value("${job.submit.concurrent:1}")
    private int jobSubmitConcurrent;

    @Value("${job.graph.builder:false}")
    private boolean jobGraphBuilderSwitch;

    @Value("${job.executor.pool.core.size:20}")
    private Integer jobExecutorPoolCorePoolSize;

    @Value("${job.executor.pool.maximum.size:20}")
    private Integer jobExecutorPoolMaximumPoolSize;

    @Value("${job.executor.pool.keep.alive.time:1000}")
    private Integer jobExecutorPoolKeepAliveTime;

    @Value("${job.executor.pool.queue.size:1000}")
    private Integer jobExecutorPoolQueueSize;

    @Value("${job.status.check.interval:3500}")
    private Long jobStatusCheckInterVal;

    @Value("${retry.frequency:3}")
    private int retryFrequency;

    @Value("${retry.interval:3500}")
    private Long retryInterval;

    @Value("${check.job.max.priority.strategy:false}")
    private boolean checkJobMaxPriorityStrategy;

    @Value("${task.status.dealer.pool.size:5}")
    private int taskStatusDealerPoolSize;

    @Value("${test.connect.timeout:5}")
    private int testConnectTimeout;

    @Value("${build.job.retry:5}")
    private int buildJobErrorRetry;

    @Value("${component.jdbc.replace:/default}")
    private String componentJdbcToReplace;

    @Value("${dataSource.min.evictable.idle.time.millis:300000}")
    private Integer minEvictableIdleTimeMillis;

    @Value("${dataSource.time.between.eviction.runs.millis:60000}")
    private Integer timeBetweenEvictionRunsMillis;

    @Value("${max.level:20}")
    private Integer maxLevel;

    @Value("${fork.join.timeout:300}")
    private Long forkJoinResultTimeOut;

    @Value("${job.schedule:true}")
    private boolean openJobSchedule;

    @Value("${dataSource.keep.alive:true}")
    private boolean keepAlive;

    @Value("${dataSource.remove.abandoned:true}")
    private boolean removeAbandoned;

    @Value("${dataSource.remove.abandoned.timeout:120}")
    private Integer removeAbandonedTimeout;

    @Value("${dataSource.test.while.idle:true}")
    private boolean testWhileIdle;

    @Value("${dataSource.test.on.borrow:true}")
    private boolean testOnBorrow;

    @Value("${dataSource.test.on.return:true}")
    private boolean testOnReturn;

    @Value("${dataSource.pool.prepared.statements:true}")
    private boolean poolPreparedStatements;

    @Value("${dataSource.max.prepared.statement.per.connection.size:20}")
    private Integer maxPoolPreparedStatementPerConnectionSize;

    @Value("${batch.insert.size:200}")
    private Integer batchInsertSize;

    @Value("${batch.insert.jobjob.size:500}")
    private Integer batchJobJobInsertSize;

    @Value("${fillData.threadPool.core.pool.size:2}")
    private Integer fillDataThreadPoolCorePoolSize;

    @Value("${fillData.threadPool.max.pool.size:20}")
    private Integer maxFillDataThreadPoolSize;

    @Value("${fillData.threadPool.queue.size:100}")
    private Integer fillDataQueueCapacity;

    @Value("${fill.data.limit.size:2000}")
    private Integer fillDataLimitSize;

    @Value("${fillData.max.level.size:1000}")
    private Integer fillDataRootTaskMaxLevel;

    @Value("${build.pool.core.pool.size:10}")
    private Integer graphBuildPoolCorePoolSize;

    @Value("${build.pool.maximum.pool.size:20}")
    private Integer graphBuildPoolMaximumPoolSize;

    @Value("${build.pool.queue.size:1000}")
    private Integer graphBuildPoolQueueSize;

    @Value("${max.task.build.thread:20}")
    private Integer maxTaskBuildThread;

    @Value("${job.graph.task.limit.size:50}")
    private Integer jobGraphTaskLimitSize;

    @Value("${create.table.type:parquet}")
    private String createTableType;

    @Value("${hdfs.batch.path:/taier/}")
    private String hdfsBatchPath;

    @Value("${temp.path:#{systemProperties['user.dir']}/temp}")
    private String tempDir;

    @Value("${explain.enable:true}")
    private Boolean explainEnable;

    @Value("${datasource.plugin.path:}")
    private String dataSourcePluginPath;

    @Value("${plugin.path:#{systemProperties['user.dir']}/pluginLibs}")
    private String pluginPath;


    @Override
    public void afterPropertiesSet() throws Exception {
        ClientCache.setUserDir(getDataSourcePluginPath());
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

    public long getJobLackingInterval() {
        String intervalObj = environment.getProperty("job.lacking.interval");
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

    public Environment getEnvironment() {
        return environment;
    }

    public String getJdbcDriverClassName() {
        return jdbcDriverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public String getHadoopUserName() {
        return hadoopUserName;
    }

    public String getHdfsTaskPath() {
        return hdfsTaskPath;
    }

    public String getNodeZkAddress() {
        return nodeZkAddress;
    }

    public String getJobGraphBuildCron() {
        return jobGraphBuildCron;
    }

    public int getJobAcquireQueueJobInterval() {
        return jobAcquireQueueJobInterval;
    }

    public int getJobCycTimeGap() {
        return jobCycTimeGap;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public long getJobStoppedDelay() {
        return jobStoppedDelay;
    }

    public int getJobStoppedRetry() {
        return jobStoppedRetry;
    }

    public long getJobRestartDelay() {
        return jobRestartDelay;
    }

    public long getJobLackingDelay() {
        return jobLackingDelay;
    }

    public long getJobPriorityStep() {
        return jobPriorityStep;
    }

    public int getJobLackingCountLimited() {
        return jobLackingCountLimited;
    }

    public long getJobSubmitExpired() {
        return jobSubmitExpired;
    }

    public long getJobLogDelay() {
        return jobLogDelay;
    }

    public String getComputeResourcePlain() {
        return computeResourcePlain;
    }

    public int getJobSubmitConcurrent() {
        return jobSubmitConcurrent;
    }

    public boolean isJobGraphBuilderSwitch() {
        return jobGraphBuilderSwitch;
    }

    public Integer getJobExecutorPoolCorePoolSize() {
        return jobExecutorPoolCorePoolSize;
    }

    public Integer getJobExecutorPoolMaximumPoolSize() {
        return jobExecutorPoolMaximumPoolSize;
    }

    public Integer getJobExecutorPoolKeepAliveTime() {
        return jobExecutorPoolKeepAliveTime;
    }

    public Integer getJobExecutorPoolQueueSize() {
        return jobExecutorPoolQueueSize;
    }

    public Long getJobStatusCheckInterVal() {
        return jobStatusCheckInterVal;
    }

    public int getRetryFrequency() {
        return retryFrequency;
    }

    public Long getRetryInterval() {
        return retryInterval;
    }

    public boolean isCheckJobMaxPriorityStrategy() {
        return checkJobMaxPriorityStrategy;
    }

    public int getTaskStatusDealerPoolSize() {
        return taskStatusDealerPoolSize;
    }

    public int getTestConnectTimeout() {
        return testConnectTimeout;
    }

    public int getBuildJobErrorRetry() {
        return buildJobErrorRetry;
    }

    public String getComponentJdbcToReplace() {
        return componentJdbcToReplace;
    }

    public Integer getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public Long getForkJoinResultTimeOut() {
        return forkJoinResultTimeOut;
    }

    public boolean isOpenJobSchedule() {
        return openJobSchedule;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public Integer getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public Integer getBatchInsertSize() {
        return batchInsertSize;
    }

    public Integer getBatchJobJobInsertSize() {
        return batchJobJobInsertSize;
    }

    public Integer getFillDataThreadPoolCorePoolSize() {
        return fillDataThreadPoolCorePoolSize;
    }

    public Integer getMaxFillDataThreadPoolSize() {
        return maxFillDataThreadPoolSize;
    }

    public Integer getFillDataQueueCapacity() {
        return fillDataQueueCapacity;
    }

    public Integer getFillDataLimitSize() {
        return fillDataLimitSize;
    }

    public Integer getFillDataRootTaskMaxLevel() {
        return fillDataRootTaskMaxLevel;
    }

    public Integer getGraphBuildPoolCorePoolSize() {
        return graphBuildPoolCorePoolSize;
    }

    public Integer getGraphBuildPoolMaximumPoolSize() {
        return graphBuildPoolMaximumPoolSize;
    }

    public Integer getGraphBuildPoolQueueSize() {
        return graphBuildPoolQueueSize;
    }

    public Integer getMaxTaskBuildThread() {
        return maxTaskBuildThread;
    }

    public Integer getJobGraphTaskLimitSize() {
        return jobGraphTaskLimitSize;
    }

    public String getCreateTableType() {
        return createTableType;
    }

    public String getHdfsBatchPath() {
        return hdfsBatchPath;
    }

    public String getTempDir() {
        return tempDir;
    }

    public Boolean getExplainEnable() {
        return explainEnable;
    }

    public String getDataSourcePluginPath() {
        return dataSourcePluginPath;
    }

    public String getPluginPath() {
        return pluginPath;
    }
}
