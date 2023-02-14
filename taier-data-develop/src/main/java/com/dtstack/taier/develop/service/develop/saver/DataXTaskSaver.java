package com.dtstack.taier.develop.service.develop.saver;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.develop.utils.develop.CreateJsonFileUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * @Author: 裴声声
 * @CreateTime: 2022-10-11  20:37
 * @Description: 痛，太痛了
 * @Version: 1.0
 */
@Component
public class DataXTaskSaver extends AbstractTaskSaver{

    @Autowired
    private UserService userService;

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.DATAX);
    }

    @Override
    public TaskResourceParam beforeProcessing(TaskResourceParam taskResourceParam) {


        // 如果是修改任务的基本属性（目录、名称），禁止处理任务信息
        if (BooleanUtils.isTrue(taskResourceParam.getEditBaseInfo())
                && Objects.nonNull(taskResourceParam.getId())
                && taskResourceParam.getId() > 0) {
            return taskResourceParam;
        }

        dealSqlTask(taskResourceParam);

        dealScheduleConf(taskResourceParam);

        return taskResourceParam;
    }

    @Override
    public void afterProcessing(TaskResourceParam taskResourceParam, TaskVO taskVO) {
        String job_home = "/Users/dtstack/ide/Taier/job";
        boolean successCreateJson = CreateJsonFileUtil.createJsonFile(taskVO.getSqlText(), job_home, taskVO.getName());
        if (!successCreateJson){
            throw new TaierDefineException("创建datax.json文件失败");
        }
    }

    /**
     * 创建任务
     *
     * @param taskParam
     * @return
     */
    private void dealSqlTask(TaskResourceParam taskParam) {
        if (StringUtils.isNotBlank(taskParam.getSqlText())) {
            return;
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String initSqlTask = String.format(SQL_NOTE_TEMPLATE, taskParam.getName(), EScheduleJobType.getByTaskType(taskParam.getTaskType()).getName(),
                userService.getUserName(taskParam.getCreateUserId()), sdf.format(System.currentTimeMillis()),
                (StringUtils.isBlank(taskParam.getTaskDesc()) ? "" : taskParam.getTaskDesc().replace("\n", " ")));
        taskParam.setSqlText(initSqlTask);
    }

    /**
     * 处理调度信息
     *
     * @param taskParam
     */
    private void dealScheduleConf(TaskResourceParam taskParam) {
        if (StringUtils.isNotBlank(taskParam.getScheduleConf())) {
            return;
        }
        taskParam.setScheduleConf(DEFAULT_SCHEDULE_CONF);
    }
}
