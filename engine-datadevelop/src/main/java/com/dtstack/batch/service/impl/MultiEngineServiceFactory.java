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

    @Resource(name = "libraSqlBuildService")
    private ISqlBuildService libraSqlBuildService;

    @Resource(name = "hiveSqlBuildService")
    private ISqlBuildService hiveSqlBuildService;

    @Resource(name = "batchSparkSqlExeService")
    private ISqlExeService batchSparkSqlExeService;

    @Resource(name = "batchLibraSqlExeService")
    private ISqlExeService batchLibraSqlExeService;

    @Resource(name = "batchTiDBSqlExeService")
    private ISqlExeService batchTiDBSqlExeService;

    @Resource(name = "batchOracleSqlExeService")
    private ISqlExeService batchOracleSqlExeService;

    @Resource(name = "batchGreenplumSqlExeService")
    private ISqlExeService batchGreenplumSqlExeService;

    @Resource(name = "batchHiveSqlExeService")
    private ISqlExeService batchHiveSqlExeService;

    @Resource(name = "batchImpalaSqlExeService")
    private ISqlExeService batchImpalaSqlExeService;

    @Resource(name = "batchInceptorSqlExeService")
    private ISqlExeService batchInceptorSqlExeService;

    @Resource(name = "batchHiveTablePartitionService")
    private ITablePartitionService batchHiveTablePartitionService;

    @Resource(name = "batchHadoopJobExeService")
    private IBatchJobExeService batchHadoopJobExeService;

    @Resource(name = "batchLibraJobExeService")
    private IBatchJobExeService batchLibraJobExeService;

    @Resource(name = "batchTiDBJobExeService")
    private IBatchJobExeService batchTiDBJobExeService;

    @Resource(name = "batchOracleJobExeService")
    private IBatchJobExeService batchOracleJobExeService;

    @Resource(name = "batchGreenplumJobExeService")
    private IBatchJobExeService batchGreenplumJobExeService;

    @Resource(name = "batchHadoopSelectSqlService")
    private IBatchSelectSqlService batchHadoopSelectSqlService;

    @Resource(name = "batchLibraSelectSqlService")
    private IBatchSelectSqlService batchLibraSelectSqlService;

    @Resource(name = "batchTiDBSelectSqlService")
    private IBatchSelectSqlService batchTiDBSelectSqlService;

    @Resource(name = "batchOracleSelectSqlService")
    private IBatchSelectSqlService batchOracleSelectSqlService;

    @Resource(name = "batchGreenplumSelectSqlService")
    private IBatchSelectSqlService batchGreenplumSelectSqlService;

    @Resource(name = "hadoopDataDownloadService")
    private IDataDownloadService hadoopDataDownloadService;

    @Resource(name = "libraDataDownLoadService")
    private IDataDownloadService libraDataDownLoadService;

    @Resource(name = "impalaDownloadService")
    private IDataDownloadService impalaDownloadService;

    @Resource(name = "batchInceptorDataDownloadService")
    private IDataDownloadService batchInceptorDataDownloadService;

    @Resource(name = "batchTiDBDataDownLoadService")
    private IDataDownloadService batchTiDBDataDownLoadService;

    @Resource(name = "batchOracleDataDownLoadService")
    private IDataDownloadService batchOracleDataDownLoadService;

    @Resource(name = "batchHadoopTaskService")
    private ITaskService batchHadoopTaskService;

    @Resource(name = "hadoopProjectService")
    public IProjectService hadoopProjectService;

    @Resource(name = "libraProjectService")
    private IProjectService libraProjectService;

    @Resource(name = "tiDBProjectService")
    private IProjectService tiDBProjectService;

    @Resource(name = "oracleProjectService")
    private IProjectService oracleProjectService;

    @Resource(name = "greenplumProjectService")
    private IProjectService greenplumProjectService;

    @Resource(name = "batchHiveFunctionService")
    private IFunctionService batchHiveFunctionService;

    @Resource(name = "batchGreenplumFunctionService")
    private IFunctionService batchGreenplumFunctionService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;


    public ISqlBuildService getSqlBuildService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return hiveSqlBuildService;
        }
        if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return libraSqlBuildService;
        }

        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public IProjectService getProjectService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return hadoopProjectService;
        }
        if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return libraProjectService;
        }
        if (MultiEngineType.TIDB.getType() == multiEngineType) {
            return tiDBProjectService;
        }
        if (MultiEngineType.ORACLE.getType() == multiEngineType) {
            return oracleProjectService;
        }
        if (MultiEngineType.GREENPLUM.getType() == multiEngineType) {
            return greenplumProjectService;
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
            if (EJobType.IMPALA_SQL.getVal().equals(taskType)) {
                return batchImpalaSqlExeService;
            }
            if (EJobType.SPARK_SQL.getVal().equals(taskType)) {
                return batchSparkSqlExeService;
            }
            if (projectId != null) {
                DataSourceType dataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
                if (DataSourceType.HIVE.equals(dataSourceType) || DataSourceType.HIVE1X.equals(dataSourceType) || DataSourceType.HIVE3X.equals(dataSourceType)) {
                    return batchHiveSqlExeService;
                }
                if (DataSourceType.IMPALA.equals(dataSourceType)) {
                    return batchImpalaSqlExeService;
                }
                if (DataSourceType.SparkThrift2_1.equals(dataSourceType)) {
                    return batchSparkSqlExeService;
                }
                if (DataSourceType.INCEPTOR.equals(dataSourceType)){
                    return batchInceptorSqlExeService;
                }
            }
        }
        if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return batchLibraSqlExeService;
        }
        if(MultiEngineType.TIDB.getType() == multiEngineType){
            return batchTiDBSqlExeService;
        }
        if(MultiEngineType.ORACLE.getType() == multiEngineType){
            return batchOracleSqlExeService;
        }
        if(MultiEngineType.GREENPLUM.getType() == multiEngineType){
            return batchGreenplumSqlExeService;
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
        if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return batchLibraJobExeService;
        }
        if (MultiEngineType.TIDB.getType() == multiEngineType){
            return batchTiDBJobExeService;
        }
        if (MultiEngineType.ORACLE.getType() == multiEngineType){
            return batchOracleJobExeService;
        }
        if (MultiEngineType.GREENPLUM.getType() == multiEngineType){
            return batchGreenplumJobExeService;
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public IBatchSelectSqlService getBatchSelectSqlService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return batchHadoopSelectSqlService;
        }
        if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return batchLibraSelectSqlService;
        }
        if (MultiEngineType.TIDB.getType() == multiEngineType){
            return batchTiDBSelectSqlService;
        }
        if (MultiEngineType.ORACLE.getType() == multiEngineType){
            return batchOracleSelectSqlService;
        }
        if (MultiEngineType.GREENPLUM.getType() == multiEngineType){
            return batchGreenplumSelectSqlService;
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }

    public IDataDownloadService getDataDownloadService(int multiEngineType) {
        return getDataDownloadService(multiEngineType, null);
    }

    public IDataDownloadService getDataDownloadService(int multiEngineType, Integer otherTypes) {
        if (null != otherTypes && otherTypes != 0) {
            if (SelectSqlTypeEnum.IMPALA.getType().equals(otherTypes)) {
                return impalaDownloadService;
            }
            if (SelectSqlTypeEnum.INCEPTOR.getType().equals(otherTypes)) {
                return batchInceptorDataDownloadService;
            }
        }
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return hadoopDataDownloadService;
        }
        if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return libraDataDownLoadService;
        }
        if(MultiEngineType.TIDB.getType() == multiEngineType){
            return batchTiDBDataDownLoadService;
        }
        if(MultiEngineType.ORACLE.getType() == multiEngineType){
            return batchOracleDataDownLoadService;
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
        } else if (MultiEngineType.GREENPLUM.getType() == multiEngineType) {
            return batchGreenplumFunctionService;
        }

        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }
}
