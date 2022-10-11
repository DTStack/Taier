package com.dtstack.taier.develop.service.develop.runner;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SparkSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.engine.JdbcInfo;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/13
 */
@Component
public class SparkSqlTaskRunner extends HadoopJdbcTaskRunner {

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.SPARK_SQL);
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema, Long datasourceId) {
        JdbcInfo jdbcInfo = getJdbcInCluster(tenantId, EComponentType.SPARK_THRIFT, "");
        JSONObject hdfsConfig = clusterService.getConfigByKey(tenantId, EComponentType.HDFS.getConfName(), null);
        String currentDb = "";
        if (useSchema) {
            currentDb = getCurrentDb(tenantId, taskType);
        }
        return getSparkSource(jdbcInfo, currentDb, hdfsConfig);
    }

    public ISourceDTO getSparkSource(JdbcInfo jdbcInfo, String dbName, JSONObject config) {
        return SparkSourceDTO.builder()
                .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                .sourceType(DataSourceType.SparkThrift2_1.getVal())
                .username(jdbcInfo.getUsername())
                .password(jdbcInfo.getPassword())
                .schema(dbName)
                .kerberosConfig(jdbcInfo.getKerberosConfig())
                .defaultFS(config.getString("fs.defaultFS"))
                .config(config.toJSONString())
                .build();
    }


}
