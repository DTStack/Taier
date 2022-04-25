package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianyi
 * @version 1.0
 * @date 2021/1/3 6:55 下午
 */
public class TaskStatusSearchVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务名称")
    private String taskName = "";

    @ApiModelProperty(value = "任务类型")
    private List<Integer> type = new ArrayList<>();

    @ApiModelProperty(value = "任务状态")
    private List<Integer> statusList;

    @ApiModelProperty(value = "组件版本")
    private List<String> componentVersion;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public List<Integer> getType() {
        return type;
    }

    public void setType(List<Integer> type) {
        this.type = type;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public List<String> getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(List<String> componentVersion) {
        this.componentVersion = componentVersion;
    }
}
