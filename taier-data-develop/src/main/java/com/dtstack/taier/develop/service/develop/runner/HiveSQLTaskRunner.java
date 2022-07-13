package com.dtstack.taier.develop.service.develop.runner;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.HiveSourceDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.engine.JdbcInfo;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.Component;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/13
 */
@org.springframework.stereotype.Component
public class HiveSQLTaskRunner extends HadoopJdbcTaskRunner {

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.HIVE_SQL);
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType) {
        List<Component> components = componentService.getComponentVersionByEngineType(tenantId, taskType);
        String componentVersion = "";
        if (CollectionUtils.isNotEmpty(components)) {
            componentVersion = components.get(0).getVersionValue();
        }
        JdbcInfo jdbcInfo = getJdbcInCluster(tenantId, EScheduleJobType.HIVE_SQL.getComponentType(), componentVersion);
        JSONObject hdfsConfig = clusterService.getConfigByKey(tenantId, EComponentType.HDFS.getConfName(), null);
        return getHiveSource(jdbcInfo, getCurrentDb(tenantId, taskType), hdfsConfig, componentVersion);
    }

    public ISourceDTO getHiveSource(JdbcInfo jdbcInfo, String dbName, JSONObject config, String componentVersion) {
        return HiveSourceDTO.builder()
                .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                .sourceType(componentTypeToDataSourceType(componentVersion).getVal())
                .username(jdbcInfo.getUsername())
                .password(jdbcInfo.getPassword())
                .kerberosConfig(jdbcInfo.getKerberosConfig())
                .defaultFS(config.getString("fs.defaultFS"))
                .config(config.toJSONString())
                .build();
    }


    public DataSourceType componentTypeToDataSourceType(String version) {
        if (!StringUtils.isBlank(version)) {
            if (version.startsWith("1")) {
                return DataSourceType.HIVE1X;
            }
            if (version.startsWith("3")) {
                return DataSourceType.HIVE3X;
            }
        }
        return DataSourceType.HIVE;
    }


}
