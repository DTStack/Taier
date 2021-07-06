package com.dtstack.batch.engine.libra.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.ProjectCreateModel;
import com.dtstack.batch.domain.User;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.service.project.IProjectService;
import com.dtstack.batch.vo.ProjectEngineVO;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * libra创建项目相关 Date: 2019/6/3 Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class LibraProjectService implements IProjectService {

	public static Logger LOG = LoggerFactory.getLogger(LibraProjectService.class);

	public static final String SCHEMA_PARAM_KEY = "currentSchema";

	@Autowired
	private IJdbcService jdbcServiceImpl;

	@Autowired
	private ITableService iTableServiceImpl;

	@Autowired
	private BatchDataSourceService batchDataSourceService;

	@Autowired
	private UserService userService;


	/**
	 * pg 获取schema 列表 FIXME 是否兼容libra
	 */
	private static final String DB_SCHEMA_LIST_SQL = "SELECT n.nspname AS \"Name\" "
			+ " FROM pg_catalog.pg_namespace n " + " WHERE n.nspname !~ '^pg_' AND n.nspname <> 'information_schema'"
			+ " ORDER BY 1";

	private static final String SCHEMA_TABLE_LIST_SQL_TMPL = "SELECT tablename FROM pg_catalog.pg_tables where SCHEMANAME = '%s'";

	@Override
	public int createProject(Long projectId, String projectName, String projectDesc, Long userId, Long tenantId,
			Long dtuicTenantId, ProjectEngineVO projectEngineVO) throws Exception {

		// 导入已有项目
		String dbName = projectName;
		if (ProjectCreateModel.intrinsic.getType().equals(projectEngineVO.getCreateModel())) {
			dbName = projectEngineVO.getDatabase();
		} else {
			// 创建新项目
			iTableServiceImpl.createDatabase(dtuicTenantId, null, projectName, ETableType.LIBRA, projectDesc);
		}

		initDefaultSource(dtuicTenantId, projectId, dbName, projectDesc, tenantId, userId);
		return 0;
	}

	// 创建默认数据源
	private void initDefaultSource(Long dtuicTenantId, Long projectId, String dbName, String projectDesc,
			Long tenantId, Long userId) {

		JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, null, EJobType.LIBRA_SQL);
		String jdbcUrl = jdbcInfo.getJdbcUrl();

		Map<String, String> jdbcParam = Maps.newHashMap();
		jdbcParam.put(SCHEMA_PARAM_KEY, dbName);
		jdbcUrl = buildJdbcURLWithParam(jdbcUrl, jdbcParam);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("jdbcUrl", jdbcUrl);
		jsonObject.put("username", jdbcInfo.getUsername());
		jsonObject.put("password", jdbcInfo.getPassword());

		String dataSourceName = dbName + "_" + MultiEngineType.LIBRA.name();
		batchDataSourceService.createMateDataSource(dtuicTenantId, tenantId, projectId, userId, jsonObject.toJSONString(), dataSourceName, DataSourceType.LIBRA.getVal());
	}

	@Override
	public List<String> getRetainDB(Long dtuicTenantId,Long userId) throws Exception {
		List<List<Object>> retainDBResult = jdbcServiceImpl.executeQuery(dtuicTenantId, null, EJobType.LIBRA_SQL, null, DB_SCHEMA_LIST_SQL);
		List<String> retainList = Lists.newArrayList();
		for (List<Object> objects : retainDBResult) {
			if ("name".equalsIgnoreCase(MathUtil.getString(objects.get(0)))) {
				continue;
			}
			retainList.add(MathUtil.getString(objects.get(0)));
		}
		return retainList;
	}

	@Override
	public List<String> getDBTableList(Long dtuicTenantId,Long userId, String dbName, Long projectId) throws Exception {
		User user = userService.getUser(userId);
		String tableListSQL = String.format(SCHEMA_TABLE_LIST_SQL_TMPL, dbName);
		List<List<Object>> exeResult = jdbcServiceImpl.executeQuery(dtuicTenantId,user == null ? null : user.getDtuicUserId(), EJobType.LIBRA_SQL, null, tableListSQL);
		List<String> tableList = Lists.newArrayList();
		exeResult.forEach(val -> {
			if (!"tablename".equalsIgnoreCase(MathUtil.getString(val.get(0)))) {
				tableList.add(MathUtil.getString(val.get(0)));
			}
			return;
		});
		return tableList;
	}


	/***
	 * 构建jdbc url 的参数后缀
	 * @param jdbcURL
	 * @param params
	 * @return
	 */
	private String buildJdbcURLWithParam(String jdbcURL, Map<String, String> params) {

		if (params == null || params.size() == 0) {
			return jdbcURL;
		}

		String splicingSymbol = needSplicingSymbol(jdbcURL) ? "?" : "";
		jdbcURL = jdbcURL + splicingSymbol;
		StringBuilder paramBuilder = new StringBuilder();

		params.forEach((key, value) -> {
			paramBuilder.append("&")
					.append(key)
					.append("=")
					.append(value);
		});

		String paramStr = paramBuilder.toString();
		paramStr = paramStr.replaceFirst("&", "");
		jdbcURL += paramStr;

		return jdbcURL;
	}

	/**
	 * 是否需要jdbc url 的参数拼接符 "?"
	 *
	 * @param jdbcURL
	 * @return
	 */
	private boolean needSplicingSymbol(String jdbcURL) {
		return !jdbcURL.contains("?");
	}

}
