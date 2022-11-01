/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/13
 */
@org.springframework.stereotype.Component
public class DefaultSQLTaskRunner extends JdbcTaskRunner {

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.HIVE_SQL, EScheduleJobType.OCEANBASE_SQL,
                EScheduleJobType.DORIS_SQL, EScheduleJobType.CLICK_HOUSE_SQL);
    }
}
