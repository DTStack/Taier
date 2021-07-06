package com.dtstack.batch.engine.libra.service;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.engine.rdbms.common.LibraDownloadBuilder;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.table.IDataDownloadService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author yuebai
 * @date 2019-06-10
 */
@Service
public class LibraDataDownLoadService implements IDataDownloadService {

	public static final String QUERY_SQL_TEMPLATE = "select %s from %s limit %s";

	public static final String QU0TE = "\"%s\"";

	public static final int DEFAULT_QUERY_RECORDS = 1000;

	@Resource
	private BatchSelectSqlService batchSelectSqlService;

	@Resource
	private IJdbcService jdbcServiceImpl;

	@Resource
	private ProjectEngineService projectEngineService;

	private LibraDownloadBuilder builder = new LibraDownloadBuilder();

	@Override
	public IDownload downloadSqlExeResult(String jobId, Long tenantId, Long projectId, Long dtuicTenantId, boolean needMask) {
		if (StringUtils.isEmpty(jobId)) {
			throw new RdosDefineException("当前下载只支持通过临时表查询的下载");
		}
		BatchHiveSelectSql batchHiveSelectSql = batchSelectSqlService.getByJobId(jobId, tenantId,
				Deleted.NORMAL.getStatus());
		if (Objects.isNull(batchHiveSelectSql)) {
			throw new RdosDefineException("当前下载任务不存在");
		}
		ProjectEngine projectDb = projectEngineService.getProjectDb(projectId, MultiEngineType.LIBRA.getType());
		if (Objects.isNull(projectDb)) {
			throw new RdosDefineException("libra引擎数据源为空");
		}
		return builder.createDownLoadDealer(batchHiveSelectSql.getSqlText(), dtuicTenantId, projectDb.getEngineIdentity());
	}

	@Override
	public List<Object> queryDataFromTable(Long dtuicTenantId, Long projectId, String tableName, String db, Integer num,
			List<String> fieldNameList, Boolean permissionStyle, boolean needMask) throws Exception {
		String sql = buildQuerySql(tableName, fieldNameList, num);
		List<List<Object>> queryResult = jdbcServiceImpl.executeQuery(dtuicTenantId, null, EJobType.LIBRA_SQL, db, sql);

		if (needMask) {
			return dataMask(queryResult);
		}

		return new ArrayList<>(queryResult);
	}

	private List<Object> dataMask(List<List<Object>> queryResult) {
		// TODO 数据脱敏
		return Collections.EMPTY_LIST;
	}

	private String buildQuerySql(String tableName, List<String> fieldNameList, Integer num) {
		List<String> columns = new ArrayList<>();
		for (String col : fieldNameList) {
			columns.add(String.format(QU0TE, col));
		}

		if (num == null) {
			num = DEFAULT_QUERY_RECORDS;
		}

		return String.format(QUERY_SQL_TEMPLATE, StringUtils.join(columns, ","), String.format(QU0TE, tableName), num);
	}

	@Override
	public IDownload buildIDownLoad(String jobId, Integer taskType, Long dtuicTenantId, Integer limitNum) {
		// TODO jiangbo buildIDownLoad
		return null;
	}

	@Override
	public IDownload typeLogDownloader(Long dtuicTenantId, String jobId, Integer limitNum, String logType) {
		// TODO jiangbo typeLogDownloader
		return null;
	}
}
