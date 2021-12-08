package com.dtstack.engine.master.dto.fill;

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
     * 是否忽略 cyctime
     * 默认：false
     */
    private Boolean ignoreCycTime = Boolean.FALSE;

    /**
     * 补数据工程数据
     *
     * fillDataType = 1 时必填
     */
    private List<FillDataChooseProjectDTO> projects;


    /**
     * 白名单列表
     */
    private List<FillDataChooseTaskDTO> whitelist;

    /**
     * 黑名单列表
     */
    private List<FillDataChooseTaskDTO> blacklist;

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

    public Boolean getIgnoreCycTime() {
        return ignoreCycTime;
    }

    public void setIgnoreCycTime(Boolean ignoreCycTime) {
        this.ignoreCycTime = ignoreCycTime;
    }

    public List<FillDataChooseProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<FillDataChooseProjectDTO> projects) {
        this.projects = projects;
    }

    public List<FillDataChooseTaskDTO> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<FillDataChooseTaskDTO> taskIds) {
        this.taskIds = taskIds;
    }

    public List<FillDataChooseTaskDTO> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<FillDataChooseTaskDTO> whitelist) {
        this.whitelist = whitelist;
    }

    public List<FillDataChooseTaskDTO> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<FillDataChooseTaskDTO> blacklist) {
        this.blacklist = blacklist;
    }

    public FillDataChooseTaskDTO getRootTaskId() {
        return rootTaskId;
    }

    public void setRootTaskId(FillDataChooseTaskDTO rootTaskId) {
        this.rootTaskId = rootTaskId;
    }
}
