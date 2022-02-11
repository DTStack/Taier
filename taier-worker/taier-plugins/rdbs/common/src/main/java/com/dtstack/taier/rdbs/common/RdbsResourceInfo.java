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

package com.dtstack.taier.rdbs.common;

import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.base.resource.EngineResourceInfo;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.rdbs.common.executor.RdbsExeQueue;

/**
 * Reason:
 * Date: 2018/1/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdbsResourceInfo implements EngineResourceInfo {

    private RdbsExeQueue rdbsExeQueue;

    public RdbsResourceInfo(RdbsExeQueue rdbsExeQueue){
        this.rdbsExeQueue = rdbsExeQueue;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        Boolean rs = rdbsExeQueue.checkCanSubmit();
        if (rs) {
            return JudgeResult.ok();
        }
        return JudgeResult.notOk("The execution queue is full");
    }
}
