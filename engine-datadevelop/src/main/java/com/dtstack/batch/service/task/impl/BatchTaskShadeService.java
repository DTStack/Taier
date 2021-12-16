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

import com.dtstack.batch.dao.BatchTaskShadeDao;
import com.dtstack.batch.dao.BatchTaskVersionDao;
import com.dtstack.batch.domain.BatchTaskVersionDetail;
import com.dtstack.batch.web.task.vo.result.BatchTaskShadePageQueryResultVO;
import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.domain.User;
import com.dtstack.engine.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.master.impl.UserService;
import com.dtstack.engine.master.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.pager.PageResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 从batch task 更新到batch task shade
 * --需要复制表
 * batch_task --> batch_task_shade
 * batch_task_param --> batch_task_shade
 * batch_task_resource --> batch_task_shade_shade
 * batch_task_task ---> batch_task_task
 * <p>
 * FIXME 暂时不考虑历史记录版本实现--->历史版本应该只需要存储sql信息
 * Date: 2017/8/23
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchTaskShadeService {

    @Autowired
    private BatchTaskVersionDao batchTaskVersionDao;

    @Autowired
    private UserService userService;

    @Autowired
    private com.dtstack.engine.master.impl.ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private BatchTaskShadeDao batchTaskShadeDao;

    /**
     * 分页查询已提交的任务
     */
    public PageResult<List<BatchTaskShadePageQueryResultVO>> pageQuery(ScheduleTaskShadeDTO dto) {
        PageResult pageResult = scheduleTaskShadeService.pageQuery(dto);
        if (Objects.isNull(pageResult)) {
            return null;
        }
        PageResult<List<BatchTaskShadePageQueryResultVO> > resultVOPageResult = new PageResult(pageResult.getCurrentPage(), pageResult.getPageSize(),
                pageResult.getTotalCount(), pageResult.getTotalPage(), null);
        if(Objects.isNull(pageResult.getData())){
            return resultVOPageResult;
        }

        List<ScheduleTaskShadeVO> taskShades = (List<ScheduleTaskShadeVO>) pageResult.getData();
        List<BatchTaskShadePageQueryResultVO> listData = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskShades)) {
            List<Long> userIds = Lists.newArrayList();
            List<Long> taskIds = Lists.newArrayList();
            taskShades.forEach(t -> {
                userIds.add(t.getModifyUserId());
                userIds.add(t.getOwnerUserId());
            });
            taskShades.forEach(t -> taskIds.add(t.getId()));
            Map<Long, User> userMap = userService.getUserMap(userIds);
            List<BatchTaskVersionDetail> versions = batchTaskVersionDao.getLatestTaskVersionByTaskIds(taskIds);
            Map<Long, String> descs = Maps.newHashMap();
            versions.forEach(v -> descs.put(v.getTaskId(), v.getPublishDesc()));
            for (ScheduleTaskShade taskShade : taskShades) {
                BatchTaskShadePageQueryResultVO  taskShadeResultVO = new BatchTaskShadePageQueryResultVO();
                taskShadeResultVO.setId(taskShade.getId());
                taskShadeResultVO.setTaskName(taskShade.getName());
                taskShadeResultVO.setTaskType(taskShade.getTaskType());
                taskShadeResultVO.setCreateUser(userMap.getOrDefault(taskShade.getCreateUserId(), new User()).getUserName());
                taskShadeResultVO.setChargeUser(userMap.getOrDefault(taskShade.getOwnerUserId(), new User()).getUserName());
                taskShadeResultVO.setModifyUser(userMap.getOrDefault(taskShade.getModifyUserId(), new User()).getUserName());
                taskShadeResultVO.setModifyTime(taskShade.getGmtModified());
                taskShadeResultVO.setTaskDesc(descs.get(taskShade.getId()));
                taskShadeResultVO.setIsDeleted(taskShade.getIsDeleted());
                listData.add(taskShadeResultVO);
            }
        }
        pageResult.setData(listData);
        return pageResult;
    }

    @Forbidden
    public void deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId) {
        batchTaskShadeDao.deleteByProjectId(projectId, userId);
    }
}
