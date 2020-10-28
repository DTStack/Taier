package com.dtstack.engine.master.temp;

import com.dtstack.engine.master.AbstractCommonTest;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestQuan extends AbstractCommonTest {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeService taskShadeService;

    @Test
    public void testQuan() {
//        scheduleJobService.createTodayTaskShade(219L, 2);
        scheduleJobService.testTrigger("7be2c4f8");
    }


    @Test
    public void updateInfo() {
        taskShadeService.info(225L, 2, "{\"sqlText\":\"SELECT * FROM DUAL\", \"computeType\":1,\"exeArgs\":\" null\",\"engineType\":\"mysql\", \"multiEngineType\":6,\"pluginInfo\":{\"jdbcUrl\":\"jdbc:mysql://172.16.100.115:3306/dt_assets?serverTimezone=UTC&characterEncoding=UTF-8&useSSL=false\",\"password\":\"DT@Stack#123\",\"username\":\"drpeco\",\"typeName\":\"mysql\"}}");
    }
}
