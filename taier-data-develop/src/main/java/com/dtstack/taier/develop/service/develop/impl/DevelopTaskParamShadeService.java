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
import com.dtstack.taier.common.enums.EParamType;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.DevelopSysParameter;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.mapper.DevelopTaskParamShadeMapper;
import org.apache.commons.lang.StringUtils;
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
public class DevelopTaskParamShadeService {

    @Autowired
    private DevelopTaskParamShadeMapper developTaskParamShadeDao;

    @Autowired
    private DevelopSysParamService developSysParamService;

    public void addOrUpdate(DevelopTaskParamShade developTaskParamShade) {
        if (StringUtils.isBlank(developTaskParamShade.getParamCommand())) {
            throw new TaierDefineException("自定义参数赋值不能为空");
        }
        DevelopTaskParamShade dbTaskParam = developTaskParamShadeDao.selectOne(Wrappers.lambdaQuery(DevelopTaskParamShade.class)
                                    .eq(DevelopTaskParamShade::getTaskId, developTaskParamShade.getTaskId())
                                    .eq(DevelopTaskParamShade::getType, developTaskParamShade.getType())
                                    .eq(DevelopTaskParamShade::getParamName, developTaskParamShade.getParamName())
                                    .eq(DevelopTaskParamShade::getIsDeleted, Deleted.NORMAL.getStatus())
                                    .last("limit 1"));
        if (Objects.nonNull(dbTaskParam)) {
            dbTaskParam.setParamCommand(developTaskParamShade.getParamCommand());
            dbTaskParam.setGmtModified(new Timestamp(System.currentTimeMillis()));
            developTaskParamShadeDao.updateById(dbTaskParam);
        } else {
            developTaskParamShade.setIsDeleted(Deleted.NORMAL.getStatus());
            developTaskParamShadeDao.insert(developTaskParamShade);
        }
    }

    public List<DevelopTaskParamShade> getTaskParam(long taskId) {

        List<DevelopTaskParamShade> taskParamShades = developTaskParamShadeDao.selectList(Wrappers.lambdaQuery(DevelopTaskParamShade.class)
                                    .eq(DevelopTaskParamShade::getTaskId,taskId)
                                    .eq(DevelopTaskParamShade::getIsDeleted,Deleted.NORMAL.getStatus()));

        // 特殊处理 TaskParam 系统参数
        for (DevelopTaskParamShade taskParamShade : taskParamShades) {
            if (!Objects.equals(EParamType.SYS_TYPE.getType(), taskParamShade.getType())) {
                continue;
            }

            // 将 command 属性设置为系统表的 command
            DevelopSysParameter sysParameter = developSysParamService.getBatchSysParamByName(taskParamShade.getParamName());
            taskParamShade.setParamCommand(sysParameter.getParamCommand());
        }
        return taskParamShades;
    }
}
