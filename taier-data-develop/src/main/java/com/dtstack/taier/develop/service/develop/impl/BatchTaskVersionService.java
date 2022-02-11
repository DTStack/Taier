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

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.dao.domain.BatchTaskVersion;
import com.dtstack.taier.dao.dto.BatchTaskVersionDetailDTO;
import com.dtstack.taier.dao.mapper.BatchTaskVersionDao;
import com.dtstack.taier.dao.pager.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchTaskVersionService {

    @Autowired
    private BatchTaskVersionDao batchTaskVersionDao;

    /**
     * 根据taskId 查询记录
     * @param taskId
     * @param pageQuery
     * @return
     */
    public List<BatchTaskVersionDetailDTO> listByTaskId(Long taskId, PageQuery pageQuery) {
        return batchTaskVersionDao.listByTaskId(taskId, pageQuery);
    }

    /**
     * 单条记录插入
     * @param batchTaskVersion
     * @return
     */
    public Integer insert(BatchTaskVersion batchTaskVersion) {
        return batchTaskVersionDao.insert(batchTaskVersion);
    }

    /**
     * 根据versionId 查询记录
     * @param versionId
     * @return
     */
    public BatchTaskVersionDetailDTO getByVersionId(Long versionId){
        return batchTaskVersionDao.getByVersionId(versionId);
    }

    /**
     * 根据versionIds 查询记录
     * @param versionId
     * @return
     */
    public List<BatchTaskVersionDetailDTO> getByVersionIds(List<Integer> versionId){
        return batchTaskVersionDao.getByVersionIds(versionId);
    }

    /**
     * 根据taskIds 查询记录
     * @param taskIds
     * @return
     */
    public List<BatchTaskVersionDetailDTO> getByTaskIds(List<Long> taskIds){
        return batchTaskVersionDao.getByTaskIds(taskIds);
    }

    /**
     * 根据taskIds 查询记录 不返回sqlText
     * @param taskIds
     * @return
     */
    public List<BatchTaskVersionDetailDTO> getWithoutSqlByTaskIds(List<Long> taskIds){
        return batchTaskVersionDao.getWithoutSqlByTaskIds(taskIds);
    }

    /**
     * 根据taskIds 查询记录  返回最后的版本信息
     * @param taskIds
     * @return
     */
    public List<BatchTaskVersionDetailDTO> getLatestTaskVersionByTaskIds(List<Long> taskIds){
        return batchTaskVersionDao.getLatestTaskVersionByTaskIds(taskIds);
    }

    /**
     * 根据taskId 获取 版本最大值
     * @param taskId
     * @return
     */
    public Integer getMaxVersionId(Long taskId){
        return batchTaskVersionDao.getMaxVersionId(taskId);
    }

    /**
     * 根据taskId 和版本id 获取固定记录的
     * @param taskId
     * @param versionId
     * @return
     */
    public BatchTaskVersionDetailDTO getBytaskIdAndVersionId(Long taskId, Long versionId){
        return batchTaskVersionDao.getBytaskIdAndVersionId(taskId, versionId);
    }

    /**
     * 根据taskId versionId 查询固定记录
     * @param taskId
     * @param version
     * @return
     */
    public BatchTaskVersion getByTaskIdAndVersion(Long taskId, Integer version){
        return batchTaskVersionDao.getByTaskIdAndVersion(taskId, version);
    }

}
