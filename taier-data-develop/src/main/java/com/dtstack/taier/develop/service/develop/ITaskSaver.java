package com.dtstack.taier.develop.service.develop;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/13
 */
public interface ITaskSaver {

    List<EScheduleJobType> support();

    TaskVO addOrUpdate(TaskResourceParam taskResourceParam);

    String processPublishSqlText(Long tenantId, Integer taskType, String sqlText);

}
