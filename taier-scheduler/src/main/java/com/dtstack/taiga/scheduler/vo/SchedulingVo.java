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

package com.dtstack.taiga.scheduler.vo;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-11
 */
@ApiModel
public class SchedulingVo {
    private int schedulingCode;

    private String SchedulingName;

    private List<IComponentVO> components;

    public int getSchedulingCode() {
        return schedulingCode;
    }

    public void setSchedulingCode(int schedulingCode) {
        this.schedulingCode = schedulingCode;
    }

    public String getSchedulingName() {
        return SchedulingName;
    }

    public void setSchedulingName(String schedulingName) {
        SchedulingName = schedulingName;
    }

    public List<IComponentVO> getComponents() {
        return components;
    }

    public void setComponents(List<IComponentVO> components) {
        this.components = components;
    }
}
