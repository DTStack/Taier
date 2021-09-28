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

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.AlertRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 9:43 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface AlertRecordDao {

    Integer insert(@Param("records") List<AlertRecord> alertRecords);

    Integer updateByMapAndIds(@Param("record") AlertRecord alertRecord, @Param("params") Map<String, Object> params, @Param("recordIds") List<Long> recordIds);

    Integer updateByMap(@Param("record") AlertRecord record, @Param("params") Map<String, Object> params);

    List<AlertRecord> selectQuery(@Param("record") AlertRecord queryAlertRecord);

    Long findMinIdByStatus(@Param("sendStatus") Integer sendStatus, @Param("nodeAddress") String nodeAddress, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

    List<AlertRecord> findListByStatus(@Param("sendStatusList") List<Integer> sendStatusList, @Param("nodeAddress") String nodeAddress, @Param("startDate") Long startDate, @Param("endDate") Long endDate, @Param("minId") Long minId, @Param("alertRecordSendStatus") Integer alertRecordSendStatus);
}
