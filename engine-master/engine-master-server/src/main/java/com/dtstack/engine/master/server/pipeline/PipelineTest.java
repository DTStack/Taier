package com.dtstack.engine.master.server.pipeline;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class PipelineTest extends AbstractTest {

    @Test
    public void testModelParam(){
        String pipelineConfig = "{\n" +
                "    \"params\":[\n" +
                "        \"uploadPath\",\n" +
                "        \"file\",\n" +
                "        \"jobId\"\n" +
                "    ],\n" +
                "    \"operator\":[\n" +
                "        {\n" +
                "            \"modelParam\":[\n" +
                "                \"jobparam\",\n" +
                "                \"url\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"launch-cmd\":[\n" +
                "                \"replace\",\n" +
                "                \"base64\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"exeArgs\":[\n" +
                "                \"replace\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        IPipeline pipeline = PipelineBuilder.buildPipeline(pipelineConfig);
        Assert.assertNotNull(pipeline);
        ScheduleTaskShade sqlTaskShade = Template.getScheduleTaskShadeTemplate();
        ScheduleJob job = Template.getScheduleJobTemplate();
        String extraInfo = "{\"info\":\"{\\\"launch-cmd\\\":\\\"python ${file} ${modelParam} \\\",\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[{\\\\\\\"id\\\\\\\":0,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"yyyyMMddHHmmss\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"bdp.system.cyctime\\\\\\\",\\\\\\\"taskId\\\\\\\":3631,\\\\\\\"type\\\\\\\":0}]\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 3.x --launch-cmd ${launch-cmd} --app-type Python3 --app-name LR\\\",\\\"engineType\\\":\\\"dtscript\\\",\\\"taskParams\\\":\\\"worker.memory=512m\\\\nworker.cores=1\\\\nexclusive=false\\\\nworker.num=1\\\\njob.priority=10\\\",\\\"maxRetryNum\\\":3,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"LR\\\",\\\"tenantId\\\":1,\\\"modelParam\\\":\\\"{\\\\\\\"remotePath\\\\\\\":\\\\\\\"/dtInsight/science/notebook_model/normal/3631/${bdp.system.cyctime}\\\\\\\",\\\\\\\"hadoop_hosts\\\\\\\":\\\\\\\"kudu2:50070,kudu1:50070\\\\\\\",\\\\\\\"localPath\\\\\\\":\\\\\\\"/home/admin/app/dt-center-dataScience/upload/3631/${bdp.system.cyctime}\\\\\\\",\\\\\\\"hadoop_username\\\\\\\":\\\\\\\"admin\\\\\\\"}\\\",\\\"taskId\\\":3631}\"}";
        Map<String,Object> actionParam = JSONObject.parseObject(extraInfo).getJSONObject("info").toJavaObject(Map.class);
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);


        Map<String, Object> pipelineParam = PipelineBuilder.getPipelineInitMap(pipelineConfig, job, sqlTaskShade, taskParamsToReplace, (pipelineMap) -> {
            pipelineMap.put("uploadPath", "hdfs://ns1/dtInsight/task/python_119_555_11264_s_1621252206383.py");
        });

        try {
            pipeline.execute(actionParam, pipelineParam);
            Assert.assertNotNull(actionParam);
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    public void testSql(){
        IPipeline pipeline = PipelineBuilder.buildDefaultSqlPipeline();
        Assert.assertNotNull(pipeline);
    }
}
