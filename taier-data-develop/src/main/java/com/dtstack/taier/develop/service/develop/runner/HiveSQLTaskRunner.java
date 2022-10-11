package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.google.common.collect.Lists;

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
}
