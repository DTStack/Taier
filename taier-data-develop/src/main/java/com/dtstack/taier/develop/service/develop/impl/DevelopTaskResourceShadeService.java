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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.BatchTaskResourceShade;
import com.dtstack.taier.dao.mapper.DevelopTaskResourceShadeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DevelopTaskResourceShadeService {

    private static Logger logger = LoggerFactory.getLogger(DevelopTaskResourceShadeService.class);

    @Autowired
    private DevelopTaskResourceShadeMapper developTaskResourceShadeDao;

    public void addOrUpdate(BatchTaskResourceShade batchTaskResourceShade) {
        if (batchTaskResourceShade.getId()!= null && batchTaskResourceShade.getId()>0) {
            //查询是否传入参数有问题
            BatchTaskResourceShade one = developTaskResourceShadeDao.selectById(batchTaskResourceShade.getId());
            if (one == null){
                throw new RdosDefineException(String.format("未查询到id = %s对应的记录", batchTaskResourceShade.getId()));
            }
            developTaskResourceShadeDao.updateById(batchTaskResourceShade);
        } else {
            batchTaskResourceShade.setIsDeleted(Deleted.NORMAL.getStatus());
            developTaskResourceShadeDao.insert(batchTaskResourceShade);
        }
    }

    /**
     * 根据taskId 删除任务
     *
     * @param taskId
     * @return
     */
    public Integer deleteByTaskId(Long taskId) {
        return developTaskResourceShadeDao.delete(Wrappers.lambdaQuery(BatchTaskResourceShade.class)
                .eq(BatchTaskResourceShade::getTaskId,taskId));
    }

}
