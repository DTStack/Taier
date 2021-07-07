package com.dtstack.batch.service.task.impl;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.service.ScheduleTaskTaskShadeService;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Reason:
 * Date: 2017/5/5
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

@Service
public class BatchTaskTaskShadeService {

    @Autowired
    private ScheduleTaskTaskShadeService schduleTaskTaskShadeRpcService;

    public ScheduleTaskVO displayOffSpring(Long taskId, Long projectId, Long userId, Integer level, Integer directType) {
        return schduleTaskTaskShadeRpcService.displayOffSpring(taskId, projectId, userId, level, directType, null).getData();
    }


    /**
     * 查询工作流全部节点信息 -- 依赖树
     *
     * @param taskId
     * @return
     */
    public ScheduleTaskVO getAllFlowSubTasks(Long taskId) {
        return schduleTaskTaskShadeRpcService.getAllFlowSubTasks(taskId, AppType.RDOS.getType()).getData();
    }

}
