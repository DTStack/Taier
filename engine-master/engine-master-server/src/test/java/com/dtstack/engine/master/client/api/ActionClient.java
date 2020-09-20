package com.dtstack.engine.master.client.api;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.service.ActionService;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightApi;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;

/**
 * @Auther: dazhi
 * @Date: 2020/9/19 3:20 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ActionClient extends AbstractTest {

    @Value("${http.address}")
    private String ip;

    @Value("${http.port}")
    private String port;

    @Test
    public void start() {
        try {
            String LOCALHOST_URL="http://"+ip+port;
            DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
                    .setEndpoint(LOCALHOST_URL)
                    .setToken(getToken());
            DtInsightApi api = builder.buildApi();
            ActionService apiClient = api.getApiClient(ActionService.class);
            Map<String, Object> params = getParams(getJsonString());
            ParamActionExt paramActionExt = com.dtstack.engine.common.util.PublicUtil.mapToObject(params, ParamActionExt.class);
            ApiResponse<Boolean> start = apiClient.start(paramActionExt);
            Assert.assertTrue(start.getData());
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void listJobStatusByJobIds() {
        try {
            String LOCALHOST_URL="http://"+ip+port;
            DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
                    .setEndpoint(LOCALHOST_URL)
                    .setToken(getToken());
            DtInsightApi api = builder.buildApi();
            ActionService apiClient = api.getApiClient(ActionService.class);
            List<String> jobIds = new ArrayList<>();
            jobIds.add("80246d68");
            ApiResponse<List<ActionJobStatusVO>> listApiResponse = apiClient.listJobStatusByJobIds(jobIds);
            org.springframework.util.Assert.notEmpty(listApiResponse.getData());
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }


    private Map<String, Object> getParams(String json) {
        return JSONObject.parseObject(json, HashMap.class);
    }

    private String getRandomStr() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String getToken(){
        return "eyJzdWNjZXNzIjp0cnVlLCJtZXNzYWdlIjoi5omn6KGM5oiQ5YqfIiwiZGF0YSI6eyJ1c2VySWQiOjEsInVzZXJOYW1lIjoiYWRtaW5AZHRzdGFjay5jb20iLCJlbWFpbCI6ImFkbWluQGR0c3RhY2suY29tIiwicGhvbmUiOiIxMzUyNjkyNTI4NiIsInRlbmFudElkIjoxLCJ0ZW5hbnROYW1lIjoiRFRTdGFja+enn+aItyIsInRlbmFudE93bmVyIjpmYWxzZSwidGVuYW50T3duZXJJZCI6OH19";
    }

    /**
     * 其中利用系统时间创建不同的任务ID
     * @return
     */
    private String getJsonString() {
        return  "{\n" +
                "  \"appType\": 1,\n" +
                "  \"computeType\": 1,\n" +
                "  \"engineType\": \"dtScript\",\n" +
                "  \"exeArgs\": \"--files hdfs://ns1/dtInsight/task/python_13_49_testPython_1599633003909.py --python-version 3 --app-type python3 --app-name testPython\",\n" +
                "  \"generateTime\": 1599633016613,\n" +
                "  \"isFailRetry\": true,\n" +
                "  \"lackingCount\": 0,\n" +
                "  \"maxRetryNum\": 3,\n" +
                "  \"name\": \"cronJob_testPython_20200909143000\",\n" +
                "  \"priority\": 0,\n" +
                "  \"requestStart\": 0,\n" +
                "  \"sqlText\": \"#coding=utf-8\\nprint('art of life')\\n\",\n" +
                "  \"stopJobId\": 0,\n" +
                "  \"taskParams\": \"## 每个worker所占>内存，比如512m\\r\\nworker.memory=512m\\r\\n\\n## 每个worker所占的cpu核的数量\\r\\nworker.cores=1\\r\\n\\n## 任务优先级, 范围:1-1000\\r\\njob.priority=10\",\n" +
                "  \"taskType\": 3,\n" +
                "  \"tenantId\": 15,\n" +
                "  \"userId\": 1\n" +
                "}";
    }
}
