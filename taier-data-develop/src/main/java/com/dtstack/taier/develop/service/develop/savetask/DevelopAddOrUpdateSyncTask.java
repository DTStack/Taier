package com.dtstack.taier.develop.service.develop.savetask;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;
import org.springframework.stereotype.Component;

/**
 * @Author: zhichen
 * @Date: 2022/05/29/5:14 PM
 */
@Component
public class DevelopAddOrUpdateSyncTask extends DevelopAddOrUpdateTaskTemplate {
    @Override
    public TaskResourceParam handleParam(TaskResourceParam taskResourceParam) {
        // 校验任务信息,主资源不能为空
        TaskVO taskVO = TaskMapstructTransfer.INSTANCE.TaskResourceParamToTaskVO(taskResourceParam);
        if (taskResourceParam.getUpdateSource()) {
            taskVO.setSourceStr(taskResourceParam.getSourceMap() == null ? "" : JSON.toJSONString(taskResourceParam.getSourceMap()));
            taskVO.setTargetStr(taskResourceParam.getTargetMap() == null ? "" : JSON.toJSONString(taskResourceParam.getTargetMap()));
            taskVO.setSettingStr(taskResourceParam.getSettingMap() == null ? "" : JSON.toJSONString(taskResourceParam.getSettingMap()));
        } else {
//            Task task = getOne(taskVO.getId());
//            taskVO.setSourceStr(task.getSourceStr());
//            taskVO.setTargetStr(task.getTargetStr());
//            taskVO.setSettingStr(task.getSettingStr());
        }
//        //检查密码回填操作
//        this.checkFillPassword(taskResourceParam);
//        if (EScheduleJobType.SYNC.getType().equals(taskVO.getTaskType()) || EScheduleJobType.DATA_ACQUISITION.getType().equals(taskVO.getTaskType())) {
//            setSqlTextByCreateModel(taskResourceParam, taskVO);
//        }
//        // 判断断点续传
//        addParam(taskResourceParam); //问下安陌什么时候需要
        return taskResourceParam;
    }

    @Override
    public EScheduleJobType getEScheduleJobType() {
        return EScheduleJobType.SYNC;
    }

    @Override
    public void addTask(TaskVO taskVO) {

    }

    @Override
    public void updateTask(TaskVO taskVO) {

    }
}
