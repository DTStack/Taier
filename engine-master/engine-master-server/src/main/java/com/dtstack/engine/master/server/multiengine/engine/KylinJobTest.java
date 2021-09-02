package com.dtstack.engine.master.server.multiengine.engine;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-01-12
 */
public class KylinJobTest extends AbstractTest {

    @Autowired
    private KylinJobStartTrigger jobStartTrigger;

    @Test
    public void testKylin(){
        try {
            String extraInfo = "{\"isFailRetry\":true,\"taskParamsToReplace\":\"[{\\\"gmtCreate\\\":1610445599000,\\\"gmtModified\\\":1610445599000,\\\"id\\\":1419,\\\"isDeleted\\\":0,\\\"paramCommand\\\":\\\"yyyyMMdd-1\\\",\\\"paramName\\\":\\\"bdp.system.bizdate\\\",\\\"taskId\\\":1571,\\\"type\\\":0}]\",\"sqlText\":\"\",\"computeType\":1,\"pluginInfo\":{\"password\":\"KYLIN\",\"typeName\":\"kylin\",\"hostPort\":\"http://172.16.101.17:7070\",\"connectParams\":null,\"cubeName\":\"kylin_sales_cube\",\"username\":\"ADMIN\"},\"engineType\":\"kylin\",\"taskParams\":\"\",\"maxRetryNum\":3,\"taskType\":4,\"ldapPassword\":\"admin123\",\"multiEngineType\":3,\"name\":\"regress_kylin_01\",\"tenantId\":1001,\"ldapUserName\":\"hxb\",\"taskId\":1571}";
            Map<String, Object> actionMap = JSONObject.parseObject(extraInfo,Map.class);
            ScheduleTaskShade scheduleTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
            scheduleTaskShadeTemplate.setExeArgs("{\"sourceId\":\"395\",\"cubeName\":\"kylin_sales_cube\",\"startTime\":\"\",\"endTime\":\"\",\"isUseSystemVar\":true,\"systemVar\":\"${bdp.system.bizdate}\",\"noPartition\":false}");
            scheduleTaskShadeTemplate.setEngineType(ScheduleEngineType.Kylin.getVal());
            ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
            scheduleJobTemplate.setCycTime("20210112000000");
            jobStartTrigger.readyForTaskStartTrigger(actionMap,scheduleTaskShadeTemplate,scheduleJobTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
