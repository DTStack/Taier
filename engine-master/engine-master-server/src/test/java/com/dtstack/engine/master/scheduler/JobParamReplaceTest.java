package com.dtstack.engine.master.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.AbstractTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2020/11/16 10:52 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobParamReplaceTest extends AbstractTest {

    @Autowired
    private JobParamReplace jobParamReplace;

    @Test
    public void testStopGraphBuildIsMaster() throws Exception {
        // sql 为"" 时
        String sql = "";
        List<ScheduleTaskParamShade> taskParamsToReplace = null;
        String cycTime = "20201116000000";
        String r1 = jobParamReplace.paramReplace(sql, taskParamsToReplace, cycTime);
        Assert.assertNotNull(r1);

        String infoJosn = "{\n" +
                "  \"info\": \"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[{\\\\\\\"gmtCreate\\\\\\\":1605509510000,\\\\\\\"gmtModified\\\\\\\":1605509510000,\\\\\\\"id\\\\\\\":303,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"yyyyMMdd-1\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"bdp.system.bizdate\\\\\\\",\\\\\\\"taskId\\\\\\\":1619,\\\\\\\"type\\\\\\\":0},{\\\\\\\"gmtCreate\\\\\\\":1605509510000,\\\\\\\"gmtModified\\\\\\\":1605509510000,\\\\\\\"id\\\\\\\":305,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"${bdp.system.currenttime}\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"bdp.system.runtime\\\\\\\",\\\\\\\"taskId\\\\\\\":1619,\\\\\\\"type\\\\\\\":0},{\\\\\\\"gmtCreate\\\\\\\":1605509510000,\\\\\\\"gmtModified\\\\\\\":1605509510000,\\\\\\\"id\\\\\\\":307,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"1234\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"dd\\\\\\\",\\\\\\\"taskId\\\\\\\":1619,\\\\\\\"type\\\\\\\":1}]\\\",\\\"sqlText\\\":\\\"use dev2;\\\\nSELECT ${bdp.system.bizdate},${dd},${bdp.system.runtime};\\\\n\\\",\\\"computeType\\\":1,\\\"engineType\\\":\\\"spark\\\",\\\"taskParams\\\":\\\"## Driver程序使用的CPU核数,默认为1\\\\r\\\\n# driver.cores=1\\\\r\\\\n\\\\n## Driver程序使用内存大小,默认512m\\\\r\\\\n# driver.memory=512m\\\\r\\\\n\\\\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\\\\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\\\\r\\\\n# driver.maxResultSize=1g\\\\r\\\\n\\\\n## SparkContext 启动时是否记录有效 SparkConf信息,默认false\\\\r\\\\n# logConf=false\\\\r\\\\n\\\\n## 启动的executor的数量，默认为1\\\\r\\\\nexecutor.instances=1\\\\r\\\\n\\\\n## 每个executor使用的CPU核数，默认为1\\\\r\\\\nexecutor.cores=1\\\\r\\\\n\\\\n## 每个executor内存大小,默认512m\\\\r\\\\n# executor.memory=512m\\\\r\\\\n\\\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\\\r\\\\njob.priority=10\\\\r\\\\n\\\\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\\\r\\\\n# logLevel = INFO\\\\r\\\\n\\\\n## spark中所有网络交互的最大超时时间\\\\r\\\\n# spark.network.timeout=120s\\\\r\\\\n\\\\n## executor的OffHeap内存，和spark.executor.memory配置使用\\\\r\\\\n# spark.yarn.executor.memoryOverhead\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":0,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"test\\\",\\\"tenantId\\\":1,\\\"taskId\\\":1619}\"\n" +
                "}";
        JSONObject extObject = JSONObject.parseObject(infoJosn);
        JSONObject info = extObject.getJSONObject(TaskConstant.INFO);
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        String r2 = jobParamReplace.paramReplace(sql, taskParamsToReplace, cycTime);
        Assert.assertNotNull(r2);

        sql = (String) actionParam.getOrDefault("sqlText", "");
        String r3 = jobParamReplace.paramReplace(sql, taskParamsToReplace, cycTime);
        Assert.assertNotNull(r3);
    }

}
