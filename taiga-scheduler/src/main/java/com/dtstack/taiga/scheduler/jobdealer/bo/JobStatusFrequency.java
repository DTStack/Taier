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

package com.dtstack.taiga.scheduler.jobdealer.bo;

/**
 * Reason:
 * Date: 2019/2/19
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobStatusFrequency {

    private Integer status;

    private Integer num = 0;

    private Long createTime;

    public JobStatusFrequency(Integer status){
        this.createTime = System.currentTimeMillis();
        this.status = status;
        this.num = 0;
    }

    public void resetJobStatus(Integer status) {
        this.createTime = System.currentTimeMillis();
        this.status = status;
        this.num = 0;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
