package com.dtstack.batch.vo.schedule;

import com.dtstack.batch.vo.base.PageVO;
import com.dtstack.batch.vo.fill.QueryFillDataJobListVO;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 3:42 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryTaskListVO extends PageVO {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFillDataJobListVO.class);

    /**
     * 租户
     */
    @ApiModelProperty(value = "租户id", hidden = true,required = true)
    private Long tenantId;

    /**
     * 所属用户
     */
    @ApiModelProperty(value = "所属用户")
    private Long ownerId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String name;

    /**
     * 调度状态：0 正常 1冻结 2停止
     */
    @ApiModelProperty(value = "调度状态：0 正常 1冻结 2停止", example = "0")
    private Integer scheduleStatus;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型")
    private String taskTypes;

    /**
     * 周期类型
     */
    @ApiModelProperty(value = "周期类型", hidden = true)
    private String periodTypes;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
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

    public String getPeriodTypes() {
        return periodTypes;
    }

    public void setPeriodTypes(String periodTypes) {
        this.periodTypes = periodTypes;
    }

    public List<Integer> getPeriodTypeList() {
        String periodTypes = this.periodTypes;
        if (StringUtils.isNotBlank(periodTypes)) {
            try {
                List<String> periodTypesStrList = Splitter.on(",").omitEmptyStrings().splitToList(periodTypes);
                return periodTypesStrList.stream().map(Integer::parseInt).collect(Collectors.toList());
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }
        return Lists.newArrayList();
    }
}
