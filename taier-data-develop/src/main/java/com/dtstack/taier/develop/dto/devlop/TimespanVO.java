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

package com.dtstack.taier.develop.dto.devlop;

import io.swagger.annotations.ApiModelProperty;

/**
 * 时间跨度格式化结果
 *
 * @author ：wangchuan
 * date：Created in 上午11:50 2021/4/19
 * company: www.dtstack.com
 */
public class TimespanVO {

    @ApiModelProperty(value = "格式是否正确", example = "true")
    private Boolean correct;

    @ApiModelProperty(value = "格式化后的结果,correct为true时有该值", example = "1d1s")
    private String formatResult;

    @ApiModelProperty(value = "格式化失败错误原因,correct为false时有该值", example = "xxx")
    private String msg;

    @ApiModelProperty(value = "时间跨度 单位：ms,correct为true时有该值", example = "xxx")
    private Long span;

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public String getFormatResult() {
        return formatResult;
    }

    public void setFormatResult(String formatResult) {
        this.formatResult = formatResult;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getSpan() {
        return span;
    }

    public void setSpan(Long span) {
        this.span = span;
    }
}
