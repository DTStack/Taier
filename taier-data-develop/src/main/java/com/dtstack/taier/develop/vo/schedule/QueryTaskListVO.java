/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.vo.schedule;

import com.dtstack.taier.develop.vo.base.PageVO;
import com.dtstack.taier.develop.vo.fill.QueryFillDataJobListVO;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    private Long operatorId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String name;

    /**
     * 最近修改的开始时间
     */
    @ApiModelProperty(value = "最近修改的开始时间 单位毫秒")
    private Long startModifiedTime;

    /**
     * 最近修改的结束时间
     */
    @ApiModelProperty(value = "最近修改的结束时间 单位毫秒")
    private Long endModifiedTime;


    /**
     * 调度状态：0 正常 1冻结 2停止
     */
    @ApiModelProperty(value = "调度状态：0 正常 1冻结 2停止", example = "0")
    private Integer scheduleStatus;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型")
    private List<Integer> taskTypeList;

    /**
     * 周期类型
     */
    @ApiModelProperty(value = "周期类型", hidden = true)
    private List<Integer> periodTypeList;

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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getStartModifiedTime() {
        return startModifiedTime;
    }

    public void setStartModifiedTime(Long startModifiedTime) {
        this.startModifiedTime = startModifiedTime;
    }

    public Long getEndModifiedTime() {
        return endModifiedTime;
    }

    public void setEndModifiedTime(Long endModifiedTime) {
        this.endModifiedTime = endModifiedTime;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public List<Integer> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<Integer> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public List<Integer> getPeriodTypeList() {
        return periodTypeList;
    }

    public void setPeriodTypeList(List<Integer> periodTypeList) {
        this.periodTypeList = periodTypeList;
    }
}
