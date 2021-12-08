package com.dtstack.batch.vo.fill;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleFillDataInfoVO {

    /**
     * 补数据类型： 0 批量补数据 1 工程补数据
     * 如果
     * fillDataType = 0时，taskIds字段有效。
     * fillDataType = 1 projects、whitelist、blacklist 有效
     * 必填
     */
    @NotNull(message = "fillDataType is not null")
    @Min(value = 0,message = " Supplement data type: 0 Batch supplement data 1 Project supplement data")
    @Max(value = 1,message = " Supplement data type: 0 Batch supplement data 1 Project supplement data")
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
    private List<FillDataChooseProjectVO> projects;


    /**
     * 白名单列表
     */
    private List<FillDataChooseTaskVO> whitelist;

    /**
     * 黑名单列表
     */
    private List<FillDataChooseTaskVO> blacklist;

    /**
     * 批量补数据任务列表
     *
     * fillDataType = 2 且 rootTaskId == null的时候，有效
     */
    private List<FillDataChooseTaskVO> taskIds;

    /**
     * 头节点
     *
     * fillDataType = 2 时有效，rootTaskId优先级大于taskIds
     */
    private FillDataChooseTaskVO rootTaskId;

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

    public List<FillDataChooseProjectVO> getProjects() {
        return projects;
    }

    public void setProjects(List<FillDataChooseProjectVO> projects) {
        this.projects = projects;
    }

    public List<FillDataChooseTaskVO> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<FillDataChooseTaskVO> taskIds) {
        this.taskIds = taskIds;
    }

    public List<FillDataChooseTaskVO> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<FillDataChooseTaskVO> whitelist) {
        this.whitelist = whitelist;
    }

    public List<FillDataChooseTaskVO> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<FillDataChooseTaskVO> blacklist) {
        this.blacklist = blacklist;
    }

    public FillDataChooseTaskVO getRootTaskId() {
        return rootTaskId;
    }

    public void setRootTaskId(FillDataChooseTaskVO rootTaskId) {
        this.rootTaskId = rootTaskId;
    }
}
