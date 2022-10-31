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

package com.dtstack.taier.develop.vo.fill;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 1:48 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnFillDataListVO {

    /**
     * 补数据标识
     */
    @ApiModelProperty(value = "补数据标识", example = "1")
    private Long id;

    /**
     * 补数据名称
     */
    @ApiModelProperty(value = "补数据名称")
    private String fillDataName;

    /**
     * 成功job数量
     */
    @ApiModelProperty(value = "成功job数量")
    private Long finishedJobSum;

    /**
     * 所有job数量
     */
    @ApiModelProperty(value = "所有job数量")
    private Long allJobSum;

    /**
     * 完成的job数量
     */
    @ApiModelProperty(value = "完成的job数量")
    private Long doneJobSum;

    /**
     * 补数据开始时间
     */
    @ApiModelProperty(value = "补数据开始时间", example = "2021-12-23")
    private String fromDay;

    /**
     * 补数据结束时间
     */
    @ApiModelProperty(value = "补数据结束时间", example = "2021-12-23")
    private String toDay;

    /**
     * 运行日期
     */
    @ApiModelProperty(value = "运行日期", example = "2021-12-23")
    private String runDay;

    /**
     * 补数据生成时间
     */
    @ApiModelProperty(value = "补数据生成时间", example = "2021-12-24 16:01:02")
    private String gmtCreate;

    /**
     * 操作人名称
     */
    @ApiModelProperty(value = "操作人名称", example = "admin@dtstack.com")
    private String operatorName;

    /**
     * 操作人id
     */
    @ApiModelProperty(value = "操作人id", example = "")
    private Long operatorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFillDataName() {
        return fillDataName;
    }

    public void setFillDataName(String fillDataName) {
        this.fillDataName = fillDataName;
    }

    public Long getFinishedJobSum() {
        return finishedJobSum;
    }

    public void setFinishedJobSum(Long finishedJobSum) {
        this.finishedJobSum = finishedJobSum;
    }

    public Long getAllJobSum() {
        return allJobSum;
    }

    public void setAllJobSum(Long allJobSum) {
        this.allJobSum = allJobSum;
    }

    public Long getDoneJobSum() {
        return doneJobSum;
    }

    public void setDoneJobSum(Long doneJobSum) {
        this.doneJobSum = doneJobSum;
    }

    public String getFromDay() {
        return fromDay;
    }

    public void setFromDay(String fromDay) {
        this.fromDay = fromDay;
    }

    public String getToDay() {
        return toDay;
    }

    public void setToDay(String toDay) {
        this.toDay = toDay;
    }

    public String getRunDay() {
        return runDay;
    }

    public void setRunDay(String runDay) {
        this.runDay = runDay;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
