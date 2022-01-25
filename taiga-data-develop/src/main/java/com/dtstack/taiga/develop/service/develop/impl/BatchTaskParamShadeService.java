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

package com.dtstack.taiga.develop.service.develop.impl;

import com.dtstack.taiga.common.enums.EParamType;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.BatchSysParameter;
import com.dtstack.taiga.dao.domain.BatchTaskParam;
import com.dtstack.taiga.dao.domain.BatchTaskParamShade;
import com.dtstack.taiga.dao.mapper.BatchTaskParamShadeDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * Reason:
 * Date: 2017/8/23
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchTaskParamShadeService {

    @Autowired
    private BatchTaskParamShadeDao batchTaskParamShadeDao;

    @Autowired
    private BatchSysParamService batchSysParamService;

    public void clearDataByTaskId(Long taskId) {
        batchTaskParamShadeDao.deleteByTaskId(taskId);
    }

    public void saveTaskParam(List<BatchTaskParam> paramList) {
        for (BatchTaskParam batchTaskParam : paramList) {
            BatchTaskParamShade paramShade = new BatchTaskParamShade();
            BeanUtils.copyProperties(batchTaskParam, paramShade);
            addOrUpdate(paramShade);
        }
    }

    public void addOrUpdate(BatchTaskParamShade batchTaskParamShade) {
        if (StringUtils.isBlank(batchTaskParamShade.getParamCommand())) {
            throw new RdosDefineException("自定义参数赋值不能为空");
        }
        BatchTaskParamShade dbTaskParam = batchTaskParamShadeDao.getByTypeAndName(batchTaskParamShade.getTaskId(), batchTaskParamShade.getType(), batchTaskParamShade.getParamName());
        if (Objects.nonNull(dbTaskParam)) {
            dbTaskParam.setParamCommand(batchTaskParamShade.getParamCommand());
            dbTaskParam.setGmtModified(new Timestamp(System.currentTimeMillis()));
            batchTaskParamShadeDao.update(dbTaskParam);
        } else {
            batchTaskParamShadeDao.insert(batchTaskParamShade);
        }
    }

    public List<BatchTaskParamShade> getTaskParam(long taskId) {
        List<BatchTaskParamShade> taskParamShades = batchTaskParamShadeDao.listByTaskId(taskId);

        // 特殊处理 TaskParam 系统参数
        for (BatchTaskParamShade taskParamShade : taskParamShades) {
            if (EParamType.SYS_TYPE.getType() != taskParamShade.getType()) {
                continue;
            }

            // 将 command 属性设置为系统表的 command
            BatchSysParameter sysParameter = batchSysParamService.getBatchSysParamByName(taskParamShade.getParamName());
            taskParamShade.setParamCommand(sysParameter.getParamCommand());
        }
        return taskParamShades;
    }
}
