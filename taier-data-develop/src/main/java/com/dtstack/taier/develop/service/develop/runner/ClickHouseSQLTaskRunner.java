package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author leon
 * @date 2022-10-12 17:27
 **/
@Component
public class ClickHouseSQLTaskRunner extends JdbcTaskRunner {

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.CLICK_HOUSE_SQL);
    }
}