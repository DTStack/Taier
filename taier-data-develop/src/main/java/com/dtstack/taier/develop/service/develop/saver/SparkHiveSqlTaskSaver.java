package com.dtstack.taier.develop.service.develop.saver;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.util.SqlFormatUtil;
import com.dtstack.taier.dao.domain.TenantComponent;
import com.dtstack.taier.develop.service.develop.impl.DevelopFunctionService;
import com.dtstack.taier.develop.service.develop.impl.DevelopSqlExeService;
import com.dtstack.taier.develop.service.develop.impl.DevelopTenantComponentService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: qianyi
 * @Date: 2022/05/29/5:14 PM
 */
@Component
public class SparkHiveSqlTaskSaver extends DefaultTaskSaver {

    private static final String CREATE_TEMP_FUNCTION_SQL = "%s %s";

    @Autowired
    private DevelopTenantComponentService developTenantComponentService;

    @Autowired
    private DevelopFunctionService developFunctionService;

    @Autowired
    private DevelopSqlExeService developSqlExeService;

    @Override
    public String processPublishSqlText(Long tenantId, Integer taskType, String sqlText) {
        TenantComponent tenantEngine = developTenantComponentService.getByTenantAndTaskType(tenantId, taskType);
        String sqlPlus = buildCustomFunctionSparkSql(sqlText, tenantId, taskType);
        return processSql(sqlPlus, tenantEngine.getComponentIdentity());
    }

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.SPARK_SQL, EScheduleJobType.HIVE_SQL);
    }

    /**
     * 处理spark sql自定义函数
     *
     * @param sqlText
     * @param tenantId
     * @param taskType
     * @return
     */
    public String buildCustomFunctionSparkSql(String sqlText, Long tenantId, Integer taskType) {
        String sqlPlus = SqlFormatUtil.formatSql(sqlText);
        if (EScheduleJobType.SPARK_SQL.getType().equals(taskType)) {
            String containFunction = developFunctionService.buildContainFunction(sqlText, tenantId, taskType);
            if (StringUtils.isNotBlank(containFunction)) {
                sqlPlus = String.format(CREATE_TEMP_FUNCTION_SQL,containFunction,sqlPlus);
            }
        }
        return sqlPlus;
    }

    /**
     * 逐条处理sql
     *
     * @param sqlText
     * @param database
     * @return
     */
    protected String processSql(String sqlText, String database) {
        sqlText = developSqlExeService.removeComment(sqlText);
        if (!sqlText.endsWith(";")) {
            sqlText = sqlText + ";";
        }

        List<String> sqls = SqlFormatUtil.splitSqlText(sqlText);
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("use ").append(database.toLowerCase()).append(";\n");

        if (CollectionUtils.isNotEmpty(sqls)) {
            for (String sql : sqls) {
                sql = SqlFormatUtil.formatSql(sql);
                sql = SqlFormatUtil.getStandardSql(sql);
                if (StringUtils.isEmpty(sql)) {
                    continue;
                }

                sqlBuild.append(sql).append(";\n");
            }
        }
        return sqlBuild.toString();
    }

}
