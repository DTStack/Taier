package com.dtstack.batch.vo.fill;

import com.dtstack.batch.vo.base.PageVO;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 4:31 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataJobListVO extends PageVO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FillDataJobListVO.class);

    /**
     * 补数据id
     */
    @NotNull(message = "fillId is not null")
    private Long fillId;

    /**
     * 租户id
     */
    @NotNull(message = "tenantId is not null")
    private Long tenantId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 计算时间
     */
    private Long cycStartDay;

    /**
     * 计算时间
     */
    private Long cycEndDay;

    /**
     * 工作流任务id
     */
    private List<String> flowJobIdList;

    /**
     * 用户ID 责任人
     */
    private Long userId;

    /**
     * 任务类型
     */
    private String taskTypes;

    /**
     * 状态
     */
    private String jobStatuses;

    /**
     * 按业务日期排序
     */
    private String businessDateSort;

    /**
     * 按计划时间排序
     */
    private String cycSort;

    /**
     * 按运行时长排序
     */
    private String execTimeSort;

    /**
     * 按开始时间排序
     */
    private String execStartSort;

    /**
     * 按重试次数排序
     */
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

    public List<String> getFlowJobIdList() {
        return flowJobIdList;
    }

    public void setFlowJobIdList(List<String> flowJobIdList) {
        this.flowJobIdList = flowJobIdList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(String taskTypes) {
        this.taskTypes = taskTypes;
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

    public String getJobStatuses() {
        return jobStatuses;
    }

    public void setJobStatuses(String jobStatuses) {
        this.jobStatuses = jobStatuses;
    }

    public List<Integer> getJobStatusList() {
        String jobStatuses = this.jobStatuses;
        if (StringUtils.isNotBlank(jobStatuses)) {
            try {
                List<String> jobStatusList = Splitter.on(",").omitEmptyStrings().splitToList(jobStatuses);
                return jobStatusList.stream().map(Integer::parseInt).collect(Collectors.toList());
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }
        return Lists.newArrayList();
    }

    public String getBusinessDateSort() {
        return businessDateSort;
    }

    public void setBusinessDateSort(String businessDateSort) {
        this.businessDateSort = businessDateSort;
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

    public String getRetryNumSort() {
        return retryNumSort;
    }

    public void setRetryNumSort(String retryNumSort) {
        this.retryNumSort = retryNumSort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FillDataJobListVO that = (FillDataJobListVO) o;
        return Objects.equals(fillId, that.fillId) && Objects.equals(tenantId, that.tenantId) && Objects.equals(taskName, that.taskName) && Objects.equals(cycStartDay, that.cycStartDay) && Objects.equals(cycEndDay, that.cycEndDay) && Objects.equals(flowJobIdList, that.flowJobIdList) && Objects.equals(userId, that.userId) && Objects.equals(taskTypes, that.taskTypes) && Objects.equals(jobStatuses, that.jobStatuses) && Objects.equals(businessDateSort, that.businessDateSort) && Objects.equals(cycSort, that.cycSort) && Objects.equals(execTimeSort, that.execTimeSort) && Objects.equals(execStartSort, that.execStartSort) && Objects.equals(retryNumSort, that.retryNumSort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fillId, tenantId, taskName, cycStartDay, cycEndDay, flowJobIdList, userId, taskTypes, jobStatuses, businessDateSort, cycSort, execTimeSort, execStartSort, retryNumSort);
    }

    @Override
    public String toString() {
        return "FillDataJobListVO{" +
                "fillId=" + fillId +
                ", tenantId=" + tenantId +
                ", taskName='" + taskName + '\'' +
                ", bizStartDay=" + cycStartDay +
                ", bizEndDay=" + cycEndDay +
                ", flowJobIdList=" + flowJobIdList +
                ", dutyUserId=" + userId +
                ", taskType='" + taskTypes + '\'' +
                ", jobStatuses='" + jobStatuses + '\'' +
                ", businessDateSort='" + businessDateSort + '\'' +
                ", cycSort='" + cycSort + '\'' +
                ", execTimeSort='" + execTimeSort + '\'' +
                ", execStartSort='" + execStartSort + '\'' +
                ", retryNumSort='" + retryNumSort + '\'' +
                '}';
    }
}
