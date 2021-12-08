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

package com.dtstack.batch.service.task.impl;

import com.dtstack.batch.dao.BatchTaskRecordDao;
import com.dtstack.batch.domain.BatchTaskRecord;
import com.dtstack.batch.dto.BatchTaskRecordDTO;
import com.dtstack.batch.enums.TaskOperateType;
import com.dtstack.batch.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.batch.vo.BatchTaskRecordVO;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.task.vo.result.BatchTaskRecordQueryRecordsResultVO;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.master.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchTaskRecordService {

    @Autowired
    private BatchTaskRecordDao batchTaskRecordDao;

    @Autowired
    private UserService userService;

    public void saveTaskRecord(BatchTaskRecord record) {
        batchTaskRecordDao.insert(record);
    }

    public void saveTaskRecords(List<BatchTaskRecord> records) {
        batchTaskRecordDao.insertAll(records);
    }

    public void removeTaskRecords(Long id, Long projectId, Long userId) {
        batchTaskRecordDao.deleteById(id, projectId, userId);
    }

    public BatchTaskRecordQueryRecordsResultVO queryRecords(Long taskId, Integer currentPage, Integer pageSize) {
        PageQuery<BatchTaskRecordDTO> pageQuery = new PageQuery<>(currentPage, pageSize,
                "operate_time", Sort.DESC.name());
        BatchTaskRecordDTO dto = new BatchTaskRecordDTO();
        //record 表中的tenantId和projectId亦代表着在哪个项目做的操作，故不作为条件
        dto.setTaskId(taskId);
        pageQuery.setModel(dto);
        List<BatchTaskRecord> records = batchTaskRecordDao.generalQuery(pageQuery);
        int count = batchTaskRecordDao.generalCount(pageQuery);
        Long tmpId = null;
        String tmpName = null;
        List<BatchTaskRecordVO> vos = new ArrayList<>(records.size());
        for (BatchTaskRecord record : records) {
            BatchTaskRecordVO vo = new BatchTaskRecordVO();
            vo.setOperateTime(record.getOperateTime().getTime());
            if (TaskOperateType.COMMIT.getType() == record.getRecordType().intValue()) { vo.setOperateType("提交"); }
            else if (TaskOperateType.THAW.getType() == record.getRecordType().intValue()) { vo.setOperateType("解冻"); }
            else if (TaskOperateType.FROZEN.getType() == record.getRecordType().intValue()) { vo.setOperateType("冻结"); }
            else if (TaskOperateType.CREATE.getType() == record.getRecordType().intValue()) { vo.setOperateType("创建"); }
            if (record.getOperatorId() != null && record.getOperatorId().equals(tmpId)) { vo.setOperatorName(tmpName); }
            else {
                if (record.getOperatorId() == null) {
                    vo.setOperatorName("");
                    continue;
                }
                tmpId = record.getOperatorId();
                tmpName = userService.getUserName(tmpId);
                vo.setOperatorName(tmpName);
            }
            vos.add(vo);
        }
        PageResult<List<BatchTaskRecordVO>> pageResult = new PageResult<>(vos, count, pageQuery);
        BatchTaskRecordQueryRecordsResultVO resultVO = new BatchTaskRecordQueryRecordsResultVO();
        resultVO.setCurrentPage(pageResult.getCurrentPage());
        resultVO.setData(TaskMapstructTransfer.INSTANCE.BatchTaskRecordVOListToBatchTaskRecordResultVOList(pageResult.getData()));
        resultVO.setPageSize(pageResult.getPageSize());
        resultVO.setTotalCount(pageResult.getTotalCount());
        resultVO.setTotalPage(pageResult.getTotalPage());
        return resultVO;
    }

    public void deleteByProjectId(Long projectId) {
        batchTaskRecordDao.deleteByProjectId(projectId);
    }
}
