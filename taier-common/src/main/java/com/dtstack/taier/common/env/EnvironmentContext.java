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
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * @author sishu.yss
 */
@Component("environmentContext")
@PropertySource(value = "file:${user.dir.conf}/application.properties")
@Data
public class EnvironmentContext implements InitializingBean {


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
        return environment.getProperty("mybatis.mapper-locations", "classpath*:sqlmap/**/*.xml");
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

    public String getLocalKerberosDir() {
        return environment.getProperty("local.kerberos.dir", System.getProperty("user.dir") + "/kerberosUploadTempDir");
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


    public Integer getMinEvictableIdleTimeMillis() {
        return Integer.valueOf(environment.getProperty("dataSource.min.evictable.idle.time.millis", "300000"));
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return Integer.valueOf(environment.getProperty("dataSource.time.between.eviction.runs.millis", "60000"));
    }

    /**控制任务展开层数**/
    public Integer getMaxLevel(){
        return Integer.valueOf(environment.getProperty("max.level","20"));
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


    public long getForkJoinResultTimeOut() {
        return Long.parseLong(environment.getProperty("fork.join.timeout", Long.toString(60 * 5)));
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

    public String getPluginPath() {
        return environment.getProperty("plugin.path",  System.getProperty("user.dir") + File.separator +"pluginLibs");
    }

    public int getBatchJobInsertSize() {
        return Integer.parseInt(environment.getProperty("batchJob.insert.size", "20"));
    }

    public int getBatchJobJobInsertSize() {
        return Integer.parseInt(environment.getProperty("batchJobJob.insert.size", "1000"));
    }

    public Integer getRestartOperatorRecordMaxSize() {
        return Integer.parseInt(environment.getProperty("restart.operator.record.max.size", "200"));
    }

    public int getFillDataThreadPoolCorePoolSize() {
        return Integer.parseInt(environment.getProperty("fillData.threadPool.core.pool.size", "2"));
    }

    public int getMaxFillDataThreadPoolSize() {
        return Integer.parseInt(environment.getProperty("fillData.threadPool.max.pool.size", "20"));
    }

    public int getFillDataQueueCapacity() {
        return Integer.parseInt(environment.getProperty("fillData.threadPool.queue.size", "100"));
    }


    public Integer getFillDataLimitSize() {
        return Integer.parseInt(environment.getProperty("fill.data.limit.size", "2000"));
    }

    public Integer getFillDataRootTaskMaxLevel() {
        return Integer.parseInt(environment.getProperty("fillData.max.level.size", "1000"));
    }

    public Integer getGraphBuildPoolCorePoolSize() {
        return Integer.parseInt(environment.getProperty("fillData.job.graph.build.pool.core.pool.size", "20"));
    }

    public Integer getGraphBuildPoolMaximumPoolSize() {
        return Integer.parseInt(environment.getProperty("fillData.job.graph.build.pool.maximum.pool.size", "20"));
    }

    public Integer getGraphBuildPoolQueueSize() {
        return Integer.parseInt(environment.getProperty("fillData.job.graph.build.pool.queue.size", "1000"));
    }

    public Integer getJobLimitSize() {
        return Integer.parseInt(environment.getProperty("fillData.job.limit.size", "50"));
    }

    public Integer getMaxTaskBuildThread() {
        return Integer.parseInt(environment.getProperty("fillData.job.max.task.build.thread", "20"));
    }


    /* datadevelop */


    @Value("${notify.sendtype.phone:false}")
    private Boolean notifyPhone;

    @Value("${create.table.type:parquet}")
    private String createTableType;

    @Value("${http.port:9020}")
    private Integer httpPort;

    @Value("${hadoop.user.name:admin}")
    private String hadoopUserName;

    @Value("${hdfs.batch.path:/dtInsight/batch/}")
    private String hdfsBatchPath;

    @Value("${public.service.node:}")
    private String publicServiceNode;

    @Value("${sync.log.promethues:true}")
    private Boolean syncLogPromethues;

    @Value("${kerberos.local.path:}")
    private String kerberosLocalPath;

    public String getKerberosLocalPath() {
        return StringUtils.isNotBlank(kerberosLocalPath) ? kerberosLocalPath : String.format("%s%s%s",
                System.getProperty("user.dir"), File.separator, "kerberosConf");
    }

    @Value("${kerberos.template.path:}")
    private String kerberosTemplatePath;

    public String getKerberosTemplatePath() {
        return  StringUtils.isNotBlank(kerberosLocalPath) ? kerberosTemplatePath : String.format("%s%s%s%s%s",
                System.getProperty("user.dir"), File.separator, "conf", File.separator, "kerberos");
    }

    @Value("${temp.table.lifecycle:1440}")
    private Integer tempTableLifecycle;

    @Value("${delete.life.time:7}")
    private Integer deleteLifeTime;

    @Value("${explain.enable:true}")
    private Boolean explainEnable;

    @Value("${table.limit:200}")
    private Integer tableLimit;

    /**
     * 小文件合并的备份文件删除时间，单位：天
     */
    @Value("${delete.merge.file.time:7}")
    private Long deleteMergeFileTime;

    /**
     * 数据保留天数
     */
    @Value("${data.keepDay:180}")
    private Long dataKeepDay;

    /**
     * 数据源插件地址
     */
    @Value("${datasource.plugin.path:}")
    private String dataSourcePluginPath;

    @Value("${engine.console.upload.path:${user.dir}/upload}")
    private String uploadPath;


    @Override
    public void afterPropertiesSet() throws Exception {
        ClientCache.setUserDir(getDataSourcePluginPath());
    }


}
