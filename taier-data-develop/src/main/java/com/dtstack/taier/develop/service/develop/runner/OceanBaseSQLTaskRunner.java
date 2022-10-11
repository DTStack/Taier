package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/12
 */
@Component
public class OceanBaseSQLTaskRunner extends JdbcTaskRunner {

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.OCEANBASE_SQL);
    }


}
