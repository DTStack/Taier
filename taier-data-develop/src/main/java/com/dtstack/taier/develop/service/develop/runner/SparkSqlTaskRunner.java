package com.dtstack.taier.develop.service.develop.runner;

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

}
