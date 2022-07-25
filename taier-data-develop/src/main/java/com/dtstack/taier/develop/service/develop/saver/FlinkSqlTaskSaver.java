package com.dtstack.taier.develop.service.develop.saver;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.TaskTemplateType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.service.develop.impl.FlinkTaskService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: qianyi
 * @Date: 2022/05/29/5:14 PM
 */
@Component
public class FlinkSqlTaskSaver extends AbstractTaskSaver {
    @Autowired
    private FlinkTaskService flinkTaskService;

    public static Logger LOGGER = LoggerFactory.getLogger(FlinkSqlTaskSaver.class);


    @Override
    public TaskResourceParam beforeProcessing(TaskResourceParam taskResourceParam) {
        flinkTaskService.convertTableStr(taskResourceParam);
        taskResourceParam.setTaskParams(taskResourceParam.getTaskParams() == null ? taskTemplateService.getTaskTemplate(TaskTemplateType.TASK_PARAMS.getType(), taskResourceParam.getTaskType(), taskResourceParam.getComponentVersion()).getContent() : taskResourceParam.getTaskParams());
        return taskResourceParam;
    }

    @Override
    public void afterProcessing(TaskResourceParam taskResourceParam, TaskVO taskVO) {

    }


    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.FLINK_SQL);
    }
}
