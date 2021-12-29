package com.dtstack.batch.vo.schedule;

import com.dtstack.batch.vo.base.PageVO;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/12/23 4:04 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryJobListVO extends PageVO {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryJobListVO.class);

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
     * 用户ID 责任人
     */
    @ApiModelProperty(value = "用户ID 责任人")
    private Long ownerId;

    /**
     * 计划开始时间
     */
    @ApiModelProperty(value = "计划开始时间")
    private Long cycStartDay;

    /**
     * 计划结束时间
     */
    @ApiModelProperty(value = "计划结束时间")
    private Long cycEndDay;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型,多个以逗号隔开")
    private String taskTypes;

    /**
     * 状态
     */
    @ApiModelProperty(value = "任务状态,多个以逗号隔开")
    private String jobStatuses;

    /**
     * 调度周期类型
     */
    @ApiModelProperty(value = "调度周期类型,多个以逗号隔开")
    private String taskPeriodTypes;

    /**
     * 按计划时间排序
     */
    @ApiModelProperty(value = "按计划时间排序")
    private String cycSort;

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
     * 按运行时长排序
     */
    @ApiModelProperty(value = " 按运行时长排序")
    private String execTimeSort;

    /**
     * 按重试次数排序
     */
    @ApiModelProperty(value = "按重试次数排序")
    private String retryNumSort;

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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    public String getTaskTypes() {
        return taskTypes;
    }

    public List<Integer> getTaskTypeList() {
        String taskTypes = this.taskTypes;
        if (StringUtils.isNotBlank(taskTypes)) {
            try {
                List<String> taskTypeStrList = Splitter.on(",").omitEmptyStrings().splitToList(taskTypes);
                return taskTypeStrList.stream().map(Integer::parseInt).collect(Collectors.toList());
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }
        return Lists.newArrayList();
    }

    public void setTaskTypes(String taskTypes) {
        this.taskTypes = taskTypes;
    }

    public String getJobStatuses() {
        return jobStatuses;
    }

    public List<Integer> getJobStatusList() {
        String jobStatuses = this.jobStatuses;
        if (StringUtils.isNotBlank(jobStatuses)) {
            try {
                List<String> jobStatusStrList = Splitter.on(",").omitEmptyStrings().splitToList(jobStatuses);
                return jobStatusStrList.stream().map(Integer::parseInt).collect(Collectors.toList());
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }
        return Lists.newArrayList();
    }

    public void setJobStatuses(String jobStatuses) {
        this.jobStatuses = jobStatuses;
    }

    public String getTaskPeriodTypes() {
        return taskPeriodTypes;
    }

    public List<Integer> getTaskPeriodTypeList() {
        String taskPeriodTypes = this.taskPeriodTypes;
        if (StringUtils.isNotBlank(taskPeriodTypes)) {
            try {
                List<String> taskPeriodTypesStrList = Splitter.on(",").omitEmptyStrings().splitToList(taskPeriodTypes);
                return taskPeriodTypesStrList.stream().map(Integer::parseInt).collect(Collectors.toList());
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }
        return Lists.newArrayList();
    }

    public void setTaskPeriodTypes(String taskPeriodTypes) {
        this.taskPeriodTypes = taskPeriodTypes;
    }

    public String getCycSort() {
        return cycSort;
    }

    public void setCycSort(String cycSort) {
        this.cycSort = cycSort;
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

    public String getExecTimeSort() {
        return execTimeSort;
    }

    public void setExecTimeSort(String execTimeSort) {
        this.execTimeSort = execTimeSort;
    }

    public String getRetryNumSort() {
        return retryNumSort;
    }

    public void setRetryNumSort(String retryNumSort) {
        this.retryNumSort = retryNumSort;
    }
}
