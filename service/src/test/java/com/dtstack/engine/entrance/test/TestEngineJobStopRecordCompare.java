package com.dtstack.engine.entrance.test;

import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobStopRecord;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/7/17
 */
public class TestEngineJobStopRecordCompare {

    private static final Logger logger = LoggerFactory.getLogger(TestEngineJobStopRecordCompare.class);


    /**
     * 测试 使用map直接构造RdosEngineJobStopRecord对象 对比 PublicUtil.mapToObject构造RdosEngineJobStopRecord对象 的性能差异
     * <p>
     * 执行10次 循环10万次的测试结果：map直接构造的性能好上 10倍 以上
     * 1. map直接构造耗时: 70 毫秒左右
     * 2. PublicUtil.mapToObject构造耗时：900毫秒左右
     */
    @Test
    public void testPublicUtil() throws IOException {
        String job = "{\n" +
                "    \"taskType\": 11,\n" +
                "    \"groupName\": \"default_c\",\n" +
                "    \"computeType\": 0,\n" +
                "    \"tenantId\": 1,\n" +
                "    \"engineType\": \"dtscript\",\n" +
                "    \"taskId\": \"b894c990\"\n" +
                "}";
        Map param = PublicUtil.jsonStrToObject(job, Map.class);

        long start = System.nanoTime();
        logger.info("start:" + start);
        for (int i = 0; i < 100000; i++) {
            RdosEngineJobStopRecord jobStopRecord1 = RdosEngineJobStopRecord.toEntity(param);
            RdosEngineJobStopRecord jobStopRecord2 = PublicUtil.mapToObject(param, RdosEngineJobStopRecord.class);
        }
        logger.info("end:" + System.nanoTime() + " cost:" + (System.nanoTime() - start));

    }
}
