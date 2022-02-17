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

package com.dtstack.taier.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taier.dao.domain.ScheduleJobOperatorRecord;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-07-06
 */
public interface ScheduleJobOperatorRecordMapper extends BaseMapper<ScheduleJobOperatorRecord> {

    /**
     * 扫描操作记录
     *
     * @param startSort 开始位置
     * @param nodeAddress 节点
     * @param type 操作类型
     * @param isEq 是否查询开始位置
     * @return 操作记录
     */
    List<ScheduleJobOperatorRecord> listOperatorRecord(@Param("startSort") Long startSort, @Param("nodeAddress") String nodeAddress, @Param("type") Integer type, @Param("isEq") Boolean isEq);

    Integer updateOperatorExpiredVersion(@Param("id") Long id, @Param("operatorExpired") Timestamp operatorExpired, @Param("version") Integer version);

    List<String> listByJobIds(@Param("jobIds") List<String> jobIds);

    void updateNodeAddress(@Param("nodeAddress") String nodeAddress, @Param("jobIds")List<String> value);

    Integer insertIgnoreBatch(@Param("records") Collection<ScheduleJobOperatorRecord> records);


}
