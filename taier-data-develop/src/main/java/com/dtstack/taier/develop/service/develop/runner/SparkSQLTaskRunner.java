package com.dtstack.taier.develop.service.develop.runner;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.SparkSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.engine.JdbcInfo;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/13
 */
@org.springframework.stereotype.Component
public class SparkSQLTaskRunner extends HadoopJdbcTaskRunner {

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.SPARK_SQL);
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType) {
        JdbcInfo jdbcInfo = getJdbcInCluster(tenantId, EScheduleJobType.SPARK_SQL.getComponentType(), "");
        JSONObject hdfsConfig = clusterService.getConfigByKey(tenantId, EComponentType.HDFS.getConfName(), null);
        return getSparkSource(jdbcInfo, getCurrentDb(tenantId, taskType), hdfsConfig);
    }

    public ISourceDTO getSparkSource(JdbcInfo jdbcInfo, String dbName, JSONObject config) {
        return SparkSourceDTO.builder()
                .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                .sourceType(DataSourceType.SparkThrift2_1.getVal())
                .username(jdbcInfo.getUsername())
                .password(jdbcInfo.getPassword())
                .kerberosConfig(jdbcInfo.getKerberosConfig())
                .defaultFS(config.getString("fs.defaultFS"))
                .config(config.toJSONString())
                .build();
    }




}
