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

package com.dtstack.taiga.develop.utils.develop.sync.job;

/**
 * 数据来源类型
 * Date: 2018/8/7
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum SourceType {
    //周期调度
    CRON(0),
    //补数据
    FILL(1),
    //临时查询
    TEMP_QUERY(2);

    Integer type;

    SourceType(Integer type){
        this.type = type;
    }

    public Integer getType(){
        return type;
    }
}
