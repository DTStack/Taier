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

package com.dtstack.taier.scheduler.server.action.fill;

import com.dtstack.taier.scheduler.enums.FillDataTypeEnum;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:43 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface FillDataTask {

    /**
     * 设置补数据类型
     *
     * @param fillDataType
     * @return
     */
    FillDataTypeEnum setFillDataType(Integer fillDataType);

    /**
     * 获取运行集合 R集合
     *
     * @return R集合
     */
    Set<Long> getRunList();

    /**
     * 填充集合
     *
     * @param run 需要跑的节点
     * @return all 集合
     */
    Set<Long> getAllList(Set<Long> run);

}
