package com.dtstack.taier.develop.service.develop.impl.runner;

import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.OceanBaseSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.engine.JdbcInfo;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.develop.service.develop.impl.DevelopJdbcTaskRunner;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/12
 */
@Component
public class DevelopOceanBaseSQLTaskRunner extends DevelopJdbcTaskRunner {

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.OCEANBASE_SQL);
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType) {
        JdbcInfo jdbcInfo = getJdbcInCluster(tenantId, EScheduleJobType.OCEANBASE_SQL.getComponentType(), null);
        return OceanBaseSourceDTO.builder()
                .sourceType(DataSourceType.OceanBase.getVal())
                .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), ""))
                .username(jdbcInfo.getUsername())
                .password(jdbcInfo.getPassword())
                .build();
    }
}
