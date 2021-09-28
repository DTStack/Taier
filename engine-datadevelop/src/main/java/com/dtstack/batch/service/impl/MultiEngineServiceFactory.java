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

package com.dtstack.batch.service.impl;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.engine.rdbms.service.ISqlBuildService;
import com.dtstack.batch.enums.SelectSqlTypeEnum;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.job.IBatchJobExeService;
import com.dtstack.batch.service.job.IBatchSelectSqlService;
import com.dtstack.batch.service.job.ITaskService;
import com.dtstack.batch.service.project.IProjectService;
import com.dtstack.batch.service.table.*;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * 根据对应的引擎类型获取执行实现
 * Date: 2019/5/13
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Component
public class MultiEngineServiceFactory {

    @Resource(name = "hiveSqlBuildService")
    private ISqlBuildService hiveSqlBuildService;

    @Resource(name = "batchSparkSqlExeService")
    private ISqlExeService batchSparkSqlExeService;

    @Resource(name = "batchHiveSqlExeService")
    private ISqlExeService batchHiveSqlExeService;

    @Resource(name = "batchHiveTablePartitionService")
    private ITablePartitionService batchHiveTablePartitionService;

    @Resource(name = "batchHadoopJobExeService")
    private IBatchJobExeService batchHadoopJobExeService;

    @Resource(name = "batchHadoopSelectSqlService")
    private IBatchSelectSqlService batchHadoopSelectSqlService;

    @Resource(name = "hadoopDataDownloadService")
    private IDataDownloadService hadoopDataDownloadService;

    @Resource(name = "batchHadoopTaskService")
    private ITaskService batchHadoopTaskService;

    @Resource(name = "hadoopProjectService")
    public IProjectService hadoopProjectService;

    @Resource(name = "batchHiveFunctionService")
    private IFunctionService batchHiveFunctionService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    public ISqlBuildService getSqlBuildService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return hiveSqlBuildService;
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public IProjectService getProjectService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return hadoopProjectService;
        }
        return null;
    }

    /**
     * 获取数据源类型通过 引擎类型 和 项目ID
     * tips：用于非任务相关获取DataSourceType，例如：数据地图、数据源
     *
     * @param multiEngineType
     * @param projectId
     * @return
     */
    public DataSourceType getDataSourceTypeByEngineTypeAndProjectId(Integer multiEngineType, Long projectId){
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            if (Objects.isNull(projectId)) {
                throw new RdosDefineException("projectId is not allowed null");
            }
            return batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        }
        return getDatasourceTypeNotIncludeHadoop(multiEngineType);
    }

    /**
     * 获取数据源类型通过 引擎类型 和 任务类型
     * tips：用于任务相关获取DataSourceType 例如：数据开发中HiveSql任务
     *
     * @param multiEngineType
     * @param taskType
     * @return
     */
    public DataSourceType getDataSourceTypeByEngineTypeAndTaskType(Integer multiEngineType, Integer taskType, Long projectId) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            if (EJobType.HIVE_SQL.getVal().equals(taskType)) {
                return DataSourceType.HIVE;
            } else if (EJobType.SPARK_SQL.getVal().equals(taskType)) {
                return DataSourceType.SparkThrift2_1;
            } else if (EJobType.IMPALA_SQL.getVal().equals(taskType)) {
                return DataSourceType.IMPALA;
            } else if (projectId != null){
                return batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
            }
        }
        return getDatasourceTypeNotIncludeHadoop(multiEngineType);
    }

    /**
     * 获取数据源类型通过引擎类型（除Hadoop引擎）
     * tips：Hadoop引擎获取数据源方式会在调用该方法之前处理掉
     *
     * @param multiEngineType 引擎类型
     * @return
     */
    private DataSourceType getDatasourceTypeNotIncludeHadoop(Integer multiEngineType){
        if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return DataSourceType.LIBRA;
        }
        if (MultiEngineType.TIDB.getType() == multiEngineType) {
            return DataSourceType.TiDB;
        }
        if (MultiEngineType.ORACLE.getType() == multiEngineType) {
            return DataSourceType.Oracle;
        }
        if (MultiEngineType.GREENPLUM.getType() == multiEngineType) {
            return DataSourceType.GREENPLUM6;
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public ISqlExeService getSqlExeService(int multiEngineType, Integer taskType, Long projectId) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            if (EJobType.HIVE_SQL.getVal().equals(taskType)) {
                return batchHiveSqlExeService;
            }
            if (EJobType.SPARK_SQL.getVal().equals(taskType)) {
                return batchSparkSqlExeService;
            }
            if (projectId != null) {
                DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
                if (DataSourceType.HIVE.equals(dataSourceType) || DataSourceType.HIVE1X.equals(dataSourceType) || DataSourceType.HIVE3X.equals(dataSourceType)) {
                    return batchHiveSqlExeService;
                }
                if (DataSourceType.SparkThrift2_1.equals(dataSourceType)) {
                    return batchSparkSqlExeService;
                }
            }
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public ITablePartitionService getTablePartitionService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return batchHiveTablePartitionService;
        }

        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public IBatchJobExeService getBatchJobExeService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return batchHadoopJobExeService;
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public IBatchSelectSqlService getBatchSelectSqlService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return batchHadoopSelectSqlService;
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public IDataDownloadService getDataDownloadService(int multiEngineType) {
        return getDataDownloadService(multiEngineType, null);
    }

    public IDataDownloadService getDataDownloadService(int multiEngineType, Integer otherTypes) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return hadoopDataDownloadService;
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }


    public ITaskService getTaskService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType){
            return batchHadoopTaskService;
        }
        return null;
    }

    public IFunctionService getFunctionService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return batchHiveFunctionService;
        }

        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }
}
