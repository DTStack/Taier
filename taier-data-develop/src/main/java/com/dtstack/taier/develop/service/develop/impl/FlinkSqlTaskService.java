package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.enums.TableType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.enums.develop.TaskCreateModelType;
import com.dtstack.taier.develop.flink.sql.SqlGenerateFactory;
import com.dtstack.taier.develop.flink.sql.source.param.KafkaSourceParamEnum;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.sql.formate.SqlFormatter;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service

public class FlinkSqlTaskService {

    private static final String TIME_CHARACTERISTIC = "time.characteristic=EventTime";

    private static final String PIPELINE_TIME_CHARACTERISTIC = "pipeline.time-characteristic=EventTime";


    @Autowired
    private DsInfoService dsInfoService;


    @Autowired
    private ScheduleActionService actionService;


    /**
     * 将前端接收的结果表维表转化
     */
    public void convertTableStr(TaskResourceParam taskResourceParam, TaskVO taskVO) {
        taskVO.setSourceStr(taskResourceParam.getSource() != null ? JSON.toJSONString(deal(taskResourceParam.getSource(), taskResourceParam.getComponentVersion())) : null);
        taskVO.setTargetStr(taskResourceParam.getSink() != null ? JSON.toJSONString(deal(taskResourceParam.getSink(), taskResourceParam.getComponentVersion())) : null);
        taskVO.setSideStr(taskResourceParam.getSide() != null ? JSON.toJSONString(deal(taskResourceParam.getSide(), taskResourceParam.getComponentVersion())) : null);
    }

    private List<JSONObject> deal(List<JSONObject> source, String componentVersion) {
        for (JSONObject obj : source) {
            //只有mysql oracle类型采用键值模式填写
            Integer type = obj.getInteger("type");
            if (DataSourceType.MySQL.getVal().equals(type)) {
                convertField(obj);
            } else {
                String columnsText = obj.getString("columnsText");
                obj.put("columns", parseColumnsFromText(columnsText, type, componentVersion));
            }
        }
        return source;
    }

    private void convertField(JSONObject sourceMeta) {
        if (!sourceMeta.containsKey("columns")) {
            return;
        }
        JSONArray columns = sourceMeta.getJSONArray("columns");
        if (Objects.isNull(columns) || columns.size() <= 0) {
            return;
        }
        JSONArray formatColumns = new JSONArray();
        for (int i = 0; i < columns.size(); i++) {
            JSONObject columnJson = columns.getJSONObject(i);
            String type = columnJson.getString("type");
            columnJson.put("type", StringUtils.replace(type, " ", ""));
            formatColumns.add(columnJson);
        }
        sourceMeta.put("columns", formatColumns);
    }

    private List<JSONObject> parseColumnsFromText(String columnsText, Integer dataSourceType, String componentVersion) {
        List<JSONObject> list = Lists.newArrayList();
        List<String> check = Lists.newArrayList();
        if (StringUtils.isBlank(columnsText)) {
            return list;
        }
        String[] columns = columnsText.split("\n");
        for (String column : columns) {
            if (StringUtils.isNotBlank(column)) {
                if (FlinkVersion.FLINK_112.getType().equals(componentVersion) && DataSourceType.HBASE.getVal().equals(dataSourceType)) {
                    // 1. flink 1.12 2. 数据源为 hbase
                    JSONObject obj = new JSONObject();
                    obj.put("column", column);
                    list.add(obj);
                } else {
                    //根据空格分隔字段名和类型
                    String[] s = column.trim().split("\\s+", 2);
                    if (s.length == 2) {
                        if (check.contains(s[0].trim())) {
                            throw new DtCenterDefException(String.format("field name:[%s] fill in the repeat", column));
                        } else {
                            check.add(s[0].trim());
                        }
                        JSONObject obj = new JSONObject();
                        obj.put("column", s[0].trim());
                        obj.put("type", s[1].trim());
                        list.add(obj);
                    } else {
                        throw new DtCenterDefException(String.format("field information:[%s] fill in the wrong", column));
                    }
                }
            }
        }
        return list;
    }

    private String generateCreateFlinkSql(String sourceParams, String componentVersion, TableType type) {
        StringBuilder createSql = new StringBuilder();
        if (StringUtils.isNotBlank(sourceParams)) {
            JSONArray array = JSON.parseArray(sourceParams);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                //从源表维表结果表的信息获取到 dataSource
                Long srcId = Long.parseLong(obj.getString("sourceId"));
                DsInfo dataSource = dsInfoService.dsInfoDetail(srcId);
                if (dataSource != null) {
                    String sql = SqlGenerateFactory.generateSql(dataSource, obj, componentVersion, type);
                    if (StringUtils.isNotBlank(sql)) {
                        createSql.append(sql);
                    }
                }
            }
        }
        return createSql.toString();
    }


    public String generateCreateFlinkSql(Task task) {
        StringBuilder sql = new StringBuilder();
        sql.append(generateCreateFlinkSql(task.getSourceStr(), task.getComponentVersion(), TableType.SOURCE));
        sql.append(generateCreateFlinkSql(task.getTargetStr(), task.getComponentVersion(), TableType.SINK));
        sql.append(generateCreateFlinkSql(task.getSideStr(), task.getComponentVersion(), TableType.SIDE));
        // 用户填写的sql
        sql.append(task.getSqlText());
        String sqlString = SqlFormatter.removeAnnotation(sql.toString());
        sqlString = SqlFormatter.sqlFormat(sqlString);
        return sqlString;
    }

    public StringBuilder generateSqlToScheduler(Task task) {
        StringBuilder sql = new StringBuilder();
        //将资源数据拼接到sql
        sql.append(generateAddJarSQL(task));
        if (TaskCreateModelType.GUIDE.getType().equals(task.getCreateModel())) {
            sql.append(generateCreateFlinkSql(task));
        }
        //用户填写的sql
        sql.append(task.getSqlText());
        return sql;
    }


    /**
     * 函数资源
     *
     * @param task
     * @return
     */
    private String generateAddJarSQL(Task task) {
        return "";
    }


    /**
     * ----------------------------------------------------------
     */


    /**
     * 开启任务
     *
     * @param task
     * @param externalPath
     * @return
     */
    public String startFlinkSqlTask(Task task, String externalPath) {
        //检查是否配置flink 集群
        checkFlinkConfig(task.getTenantId());
        //检查任务状态
        checkTaskStatus(task);
        //提交任务
        return sendTaskStartTrigger(task, externalPath);
    }

    private void checkFlinkConfig(Long tenantId) {
    }


    private void checkTaskStatus(Task task) {
    }

    private String sendTaskStartTrigger(Task task, String externalPath) {
        actionService.start(generateParamActionExt(task, externalPath));
        return "";
    }

    public ParamActionExt generateParamActionExt(Task task, String externalPath) {
        // 构造savepoint参数
        String taskParams = task.getTaskParams();
        JSONObject confProp = new JSONObject();
        //生成最终拼接的sql
        String sql = generateSqlToScheduler(task).toString();
        task.setSqlText(sql);
        // 统一处理engineJobType
        //  task.setTaskType(EStreamJobType.getEngineJobType(streamTask.getTaskType()));
        return generateParamActionExt(task, externalPath, taskParams);
    }


    private ParamActionExt generateParamActionExt(Task task, String externalPath, String taskParams) {
        Map<String, Object> actionParam = JsonUtils.objectToMap(task);
        // 去除默认生成的字段
        actionParam.remove("mainClass");
        actionParam.remove("class");
        // 补充其他的字段
        // actionParam.put("engineType", EngineType.getEngineName(task.getEngineType()));
        actionParam.put("tenantId", task.getTenantId());
        actionParam.put("taskParams", formatTaskParams(taskParams, task.getSourceStr(), task.getComponentVersion()));
        actionParam.put("name", task.getName());
        actionParam.put("deployMode", EDeployMode.PERJOB.getType());

        if (!Strings.isNullOrEmpty(externalPath)) {
            actionParam.put("externalPath", externalPath);
        }
        return JsonUtils.objectToObject(actionParam, ParamActionExt.class);
    }

    private String formatTaskParams(String taskParams, String sourceParam, String componentVersion) {
        List<String> params = new ArrayList<>();
        String[] tempParams = taskParams.split("\r|\n");
        for (String param : tempParams) {
            if (StringUtils.isNotEmpty(param.trim()) && !param.trim().startsWith("#") && param.contains("=")) {
                int special = param.indexOf("=");
                params.add(String.format("%s=%s", param.substring(0, special).trim(), param.substring(special + 1).trim()));
            }
        }
        if (StringUtils.isNotEmpty(sourceParam)) {
            //为时间特征为eventTime的的任务添加参数
            JSONArray array = JSON.parseArray(sourceParam);
            boolean timeCharacteristic = true;
            for (int i = 0; i < array.size(); i++) {
                JSONObject sourceJson = array.getJSONObject(i);
                String timeColumnFront = sourceJson.getString(KafkaSourceParamEnum.TIME_COLUMN.getFront());
                if (StringUtils.isNotBlank(timeColumnFront)) {
                    // flink1.12 默认时间语义是事件时间，之前是机器时间
                    String timeCharKey = FlinkVersion.FLINK_112.getType().equals(componentVersion) ?
                            PIPELINE_TIME_CHARACTERISTIC : TIME_CHARACTERISTIC;
                    if (!params.contains(timeCharKey)) {
                        if (timeCharacteristic) {
                            params.add(timeCharKey);
                            timeCharacteristic = false;
                        }
                    }
                }
            }
        }

        return StringUtils.join(params, "\n");
    }

}
