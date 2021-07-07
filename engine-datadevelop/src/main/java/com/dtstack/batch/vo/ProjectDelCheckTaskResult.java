package com.dtstack.batch.vo;

import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDelCheckTaskResult {

    /**
     * 任务名
     */
    private String taskName;

    /**
     * 任务名对应的被其他项目依赖的任务列表
     */
    private List<NotDeleteProjectVO> projectTasks;
}
