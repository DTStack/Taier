package com.dtstack.batch.engine.inceptor.service;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.engine.rdbms.inceptor.util.InceptorDownloadBuilder;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.table.IDataDownloadService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 4:49 下午 2019/10/18
 */
@Service
public class BatchInceptorDataDownloadService implements IDataDownloadService {

    public static final String QUERY_SQL_TEMPLATE = "select %s from %s limit %s";

    public static final String QU0TE = "%s";

    public static final int DEFAULT_QUERY_RECORDS = 1000;

    @Resource
    private BatchSelectSqlService batchSelectSqlService;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private ProjectEngineService projectEngineService;

    /**
     * 下载sql 执行结果
     *
     * @param jobId
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param needMask
     * @return
     */
    @Override
    public IDownload downloadSqlExeResult(String jobId, Long tenantId, Long projectId, Long dtuicTenantId, boolean needMask) {
        if (StringUtils.isEmpty(jobId)) {
            throw new RdosDefineException("当前下载只支持通过临时表查询的下载");
        }
        BatchHiveSelectSql batchHiveSelectSql = batchSelectSqlService.getByJobId(jobId, tenantId,
                Deleted.NORMAL.getStatus());
        if (null == batchHiveSelectSql) {
            throw new RdosDefineException("当前下载任务不存在");
        }
        ProjectEngine projectDb = projectEngineService.getProjectDb(projectId, MultiEngineType.HADOOP.getType());
        if (Objects.isNull(projectDb)){
            throw new RdosDefineException("当前项目未对接Hadoop引擎");
        }

        return InceptorDownloadBuilder.createDownLoadDealer(batchHiveSelectSql.getSqlText(), dtuicTenantId, projectDb.getEngineIdentity());
    }

    /**
     * 查询表数据
     *
     * @param dtuicTenantId
     * @param projectId
     * @param tableName
     * @param db
     * @param num
     * @param fieldNameList
     * @param permissionStyle
     * @param needMask
     * @return
     * @throws Exception
     */
    @Override
    public List<Object> queryDataFromTable(Long dtuicTenantId, Long projectId, String tableName, String db, Integer num, List<String> fieldNameList, Boolean permissionStyle, boolean needMask) throws Exception {
        String sql = buildQuerySql(tableName, fieldNameList, num);
        List<List<Object>> queryResult = jdbcServiceImpl.executeQuery(dtuicTenantId, null, EJobType.INCEPTOR_SQL, db, sql);
        return new ArrayList<>(queryResult);
    }

    @Override
    public IDownload buildIDownLoad(String jobId, Integer taskType, Long dtuicTenantId, Integer limitNum) {
        return null;
    }

    @Override
    public IDownload typeLogDownloader(Long dtuicTenantId, String jobId, Integer limitNum, String logType) {
        return null;
    }

    /**
     * 构建查询语句
     *
     * @param tableName
     * @param fieldNameList
     * @param num
     * @return
     */
    private String buildQuerySql(String tableName, List<String> fieldNameList, Integer num) {
        List<String> columns = new ArrayList<>();
        for (String col : fieldNameList) {
            columns.add(String.format(QU0TE, col));
        }
        if (Objects.isNull(num)) {
            num = DEFAULT_QUERY_RECORDS;
        }
        return String.format(QUERY_SQL_TEMPLATE, StringUtils.join(columns, ","), tableName, num);
    }
}
