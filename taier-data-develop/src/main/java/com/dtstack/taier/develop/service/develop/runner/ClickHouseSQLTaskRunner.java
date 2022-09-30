package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.dtcenter.loader.dto.source.ClickHouseSourceDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.engine.JdbcInfo;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author leon
 * @date 2022-09-30 11:38
 **/
@Component
public class ClickHouseSQLTaskRunner extends JdbcTaskRunner{

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.CLICK_HOUSE_SQL);
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema) {
        String currentDb = "";
        JdbcInfo jdbcInfo = getJdbcInCluster(tenantId, EScheduleJobType.CLICK_HOUSE_SQL.getComponentType(), null);
        if (useSchema) {
            currentDb = getCurrentDb(tenantId, taskType);
        }
        return ClickHouseSourceDTO.builder()
                .sourceType(DataSourceType.Clickhouse.getVal())
                .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), currentDb).trim())
                .schema(currentDb)
                .username(jdbcInfo.getUsername())
                .password(jdbcInfo.getPassword())
                .build();
    }
}
