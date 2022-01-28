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

package com.dtstack.taiga.develop.vo.console;


/**
 * @Auther: dazhi
 * @Date: 2020/7/29 8:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConsoleJobVO {
    private ConsoleJobInfoVO theJob;
    private Integer theJobIdx;
    private String nodeAddress;

    public ConsoleJobInfoVO getTheJob() {
        return theJob;
    }

    public void setTheJob(ConsoleJobInfoVO theJob) {
        this.theJob = theJob;
    }

    public Integer getTheJobIdx() {
        return theJobIdx;
    }

    public void setTheJobIdx(Integer theJobIdx) {
        this.theJobIdx = theJobIdx;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }
}
