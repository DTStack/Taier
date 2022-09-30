package com.dtstack.taier.develop.service.develop.saver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-11 20:52
 */
@Component
public class ScriptTaskSaver extends AbstractTaskSaver {
    @Autowired
    private DefaultTaskSaver defaultTaskSaver;

    @Override
    public List<EScheduleJobType> support() {
        return ImmutableList.of(EScheduleJobType.SHELL, EScheduleJobType.PYTHON);
    }

    @Override
    public TaskResourceParam beforeProcessing(TaskResourceParam taskResourceParam) {
        populateExeArgs(taskResourceParam);
        return taskResourceParam;
    }

    @Override
    public void afterProcessing(TaskResourceParam taskResourceParam, TaskVO taskVO) {
        defaultTaskSaver.afterProcessing(taskResourceParam, taskVO);
    }

    @Override
    public String processScheduleRunSqlText(Long tenantId, Integer taskType, String sqlText) {
        return sqlText;
    }

    /**
     * 补充运行参数
     *
     * @param param
     */
    private void populateExeArgs(TaskResourceParam param) {
        JSONObject exeArgs = new JSONObject();
        exeArgs.put("operateModel", param.getOperateModel());
        exeArgs.put("--app-name", param.getName());
        exeArgs.put("--input", param.getInput());
        exeArgs.put("--output", param.getOutput());
        exeArgs.put("--files", param.getResourceIdList());
        // 由前端传入的 app-type 为 python2/python3  区分 python 版本

        final String exeArgsParam = param.getExeArgs();
        if (StringUtils.isNotEmpty(exeArgsParam)) {
            final JSONObject paramObj = JSON.parseObject(exeArgsParam);
            // 如果传入了 param.getExeArgs，就追加预置参数
            paramObj.putAll(exeArgs);
            exeArgs = paramObj;
        }
        param.setExeArgs(exeArgs.toJSONString());
    }
}