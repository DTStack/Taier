package com.dtstack.taier.scheduler.dto.fill;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleFillDataInfoDTO
{

    /**
     * 补数据类型： 0 批量补数据 1 工程补数据
     * 如果
     * fillDataType = 0时，taskIds字段有效。
     * fillDataType = 1 projects、whitelist、blacklist 有效
     * 必填
     */
    private Integer fillDataType;

    /**
     * 批量补数据任务列表
     *
     * fillDataType = 2 且 rootTaskId == null的时候，有效
     */
    private List<FillDataChooseTaskDTO> taskIds;

    /**
     * 头节点
     *
     * fillDataType = 2 时有效，rootTaskId优先级大于taskIds
     */
    private FillDataChooseTaskDTO rootTaskId;

    public Integer getFillDataType() {
        return fillDataType;
    }

    public void setFillDataType(Integer fillDataType) {
        this.fillDataType = fillDataType;
    }

    public List<FillDataChooseTaskDTO> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<FillDataChooseTaskDTO> taskIds) {
        this.taskIds = taskIds;
    }

    public FillDataChooseTaskDTO getRootTaskId() {
        return rootTaskId;
    }

    public void setRootTaskId(FillDataChooseTaskDTO rootTaskId) {
        this.rootTaskId = rootTaskId;
    }
}
