package com.dtstack.taier.develop.vo.fill;

import com.dtstack.taier.develop.vo.base.PageVO;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 4:31 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryFillDataJobListVO extends PageVO {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFillDataJobListVO.class);

    /**
     * 补数据id
     */
    @NotNull(message = "fillId is not null")
    @ApiModelProperty(value = "补数据id",required = true)
    private Long fillId;

    /**
     * 租户id
     */
    @NotNull(message = "tenantId is not null")
    @ApiModelProperty(value = "租户id",hidden = true)
    private Long tenantId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 计算时间
     */
    @ApiModelProperty(value = "计算执行的开始时间")
    private Long cycStartDay;

    /**
     * 计算时间
     */
    @ApiModelProperty(value = "计算执行的结束时间")
    private Long cycEndDay;

    /**
     * 用户ID 责任人
     */
    @ApiModelProperty(value = "用户ID 责任人")
    private Long ownerId;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型,多个用逗号隔开")
    private List<Integer> taskTypeList;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态类型,多个用逗号隔开")
    private List<Integer> jobStatusList;

    /**
     * 按计划时间排序
     */
    @ApiModelProperty(value = "按计划时间排序")
    private String cycSort;

    /**
     * 按运行时长排序
     */
    @ApiModelProperty(value = "按运行时长排序")
    private String execTimeSort;

    /**
     * 按开始时间排序
     */
    @ApiModelProperty(value = "按开始时间排序")
    private String execStartSort;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String execEndSort;

    /**
     * 按重试次数排序
     */
    @ApiModelProperty(value = "按重试次数排序")
    private String retryNumSort;

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getCycStartDay() {
        return cycStartDay;
    }

    public void setCycStartDay(Long cycStartDay) {
        this.cycStartDay = cycStartDay;
    }

    public Long getCycEndDay() {
        return cycEndDay;
    }

    public void setCycEndDay(Long cycEndDay) {
        this.cycEndDay = cycEndDay;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<Integer> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<Integer> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public List<Integer> getJobStatusList() {
        return jobStatusList;
    }

    public void setJobStatusList(List<Integer> jobStatusList) {
        this.jobStatusList = jobStatusList;
    }
    public String getCycSort() {
        return cycSort;
    }

    public void setCycSort(String cycSort) {
        this.cycSort = cycSort;
    }

    public String getExecTimeSort() {
        return execTimeSort;
    }

    public void setExecTimeSort(String execTimeSort) {
        this.execTimeSort = execTimeSort;
    }

    public String getExecStartSort() {
        return execStartSort;
    }

    public void setExecStartSort(String execStartSort) {
        this.execStartSort = execStartSort;
    }

    public String getExecEndSort() {
        return execEndSort;
    }

    public void setExecEndSort(String execEndSort) {
        this.execEndSort = execEndSort;
    }

    public String getRetryNumSort() {
        return retryNumSort;
    }

    public void setRetryNumSort(String retryNumSort) {
        this.retryNumSort = retryNumSort;
    }
}
