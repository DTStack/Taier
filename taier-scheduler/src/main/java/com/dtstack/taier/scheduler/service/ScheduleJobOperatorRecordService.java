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

package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taier.dao.mapper.ScheduleJobOperatorRecordMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:47 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobOperatorRecordService extends ServiceImpl<ScheduleJobOperatorRecordMapper, ScheduleJobOperatorRecord> {

    /**
     * 扫描操作记录
     *
     * @param startSort   开始位置
     * @param nodeAddress 节点
     * @param type        操作类型
     * @param isEq        是否查询开始位置
     * @return 操作记录
     */
    public List<ScheduleJobOperatorRecord> listOperatorRecord(Long startSort, String nodeAddress, Integer type, Boolean isEq) {
        if (startSort != null && startSort >= 0) {
            return this.baseMapper.listOperatorRecord(startSort, nodeAddress, type, isEq);
        }
        return Lists.newArrayList();
    }

    public Integer updateOperatorExpiredVersion(Long id, Timestamp operatorExpired, Integer version) {
        if (id != null && id > 0 && version != null) {
            return this.baseMapper.updateOperatorExpiredVersion(id, operatorExpired, version);
        }
        return 0;
    }

    public void insertBatch(Set<ScheduleJobOperatorRecord> scheduleJobOperatorRecords) {
        if (CollectionUtils.isEmpty(scheduleJobOperatorRecords)) {
            return;
        }
        this.baseMapper.insertIgnoreBatch(scheduleJobOperatorRecords);
    }


    public void deleteById(Long stopJobId) {
        this.baseMapper.deleteById(stopJobId);
    }
}
