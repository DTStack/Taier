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

package com.dtstack.taier.common.enums;

/**
 * 任务节点层级显示
 * 0 展开上下游, 1:展开上游 2:展开下游
 * Date: 2018/3/21
 * Company: www.dtstack.com
 * @author xuchao
 */
public enum DisplayDirect {

    FATHER_CHILD(0), FATHER(1), CHILD(2);

    Integer type = 0;

    DisplayDirect(Integer type){
        this.type = type;
    }

    public Integer getType(){
        return type;
    }
}
