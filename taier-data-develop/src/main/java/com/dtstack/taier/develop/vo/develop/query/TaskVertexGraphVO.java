package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;

/**
 * @author qianyi
 * @version 1.0
 * @date 2022/5/3 6:55 下午
 */
public class TaskVertexGraphVO extends DtInsightAuthParam {

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    private Long taskId;

}
