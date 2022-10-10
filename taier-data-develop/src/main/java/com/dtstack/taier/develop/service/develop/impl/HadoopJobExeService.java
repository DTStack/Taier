package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.DevelopTaskParam;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * hadoop 相关类型Job执行
 * Date: 2019/5/17
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class HadoopJobExeService {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopJobExeService.class);

    @Autowired
    private DevelopTaskParamService developTaskParamService;

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private DatasourceService datasourceService;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final Map<Integer, String> PY_VERSION_MAP = new HashMap<>(2);

    static {
        PY_VERSION_MAP.put(2, " 2.x ");
        PY_VERSION_MAP.put(3, " 3.x ");
    }

    public void readyForTaskStartTrigger(Map<String, Object> actionParam, Long dtuicTenantId, Task task) throws Exception {

        String sql = task.getSqlText();
        sql = sql == null ? "" : sql;
        String taskParams = task.getTaskParams();
        String taskExeArgs = null;
        JSONObject syncJob = JSON.parseObject(task.getSqlText());
        taskParams = replaceSyncParll(taskParams, parseSyncChannel(syncJob));
        String job = syncJob.getString("job");

        //todo 合并（指的时候实时任务的提交任务相似的逻辑,需要调度支持接口后合并）
        // 向导模式根据job中的sourceId填充数据源信息，保证每次运行取到最新的连接信息
        job = datasourceService.setJobDataSourceInfo(job, dtuicTenantId, syncJob.getIntValue("createModel"));

        //todo checkSyncJobParams为什么要异常hadoopConfig
        if (Objects.equals(task.getTaskType(), EScheduleJobType.SYNC.getVal())) {
            List<DevelopTaskParam> taskParam = developTaskParamService.getTaskParam(task.getId());
            developTaskParamService.checkParams(developTaskParamService.checkSyncJobParams(job), taskParam);
        }
        actionParam.put("job", job);
        //设置写数据源的具体类型
        setWriterDataSourceType(actionParam, job);
        if (taskExeArgs != null) {
            actionParam.put("exeArgs", taskExeArgs);
        }
        actionParam.put("sqlText", sql);
        actionParam.put("taskParams", taskParams);
    }

    private Integer parseSyncChannel(JSONObject syncJob) {
        //解析出并发度---sync 消耗资源是: 并发度*1
        try {
            JSONObject jobJson = syncJob.getJSONObject("job").getJSONObject("job");
            JSONObject settingJson = jobJson.getJSONObject("setting");
            JSONObject speedJson = settingJson.getJSONObject("speed");
            return speedJson.getInteger("channel");
        } catch (Exception e) {
            LOG.error("", e);
            //默认1
            return 1;
        }

    }

    public String replaceSyncParll(String taskParams, int parallelism) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes(StandardCharsets.UTF_8)));
        properties.put("mr.job.parallelism", parallelism);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> tmp : properties.entrySet()) {
            sb.append(tmp.getKey())
                    .append(" = ")
                    .append(tmp.getValue())
                    .append(LINE_SEPARATOR);
        }
        return sb.toString();
    }

    /**
     * todo 是否可删除
     * 设置写数据源的数据源类型
     *
     * @param actionParam
     * @param job
     */
    private void setWriterDataSourceType(Map<String, Object> actionParam, String job) {
        try {
            Object sourceIdObject = JSONPath.eval(JSON.parseObject(job), "$.job.content[0].writer.parameter.sourceIds[0]");
            if (sourceIdObject != null && StringUtils.isNotBlank(sourceIdObject.toString())) {
                DsInfo data = dsInfoService.getOneById(Long.valueOf(sourceIdObject.toString()));
                if (Objects.nonNull(data)) {
                    actionParam.put("dataSourceType", data.getDataTypeCode());
                }
            }
        } catch (Exception e) {
            LOG.info("get write datasource error {} ", job, e);
        }
    }

}
