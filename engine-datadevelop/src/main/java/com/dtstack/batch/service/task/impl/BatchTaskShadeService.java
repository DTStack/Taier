package com.dtstack.batch.service.task.impl;

import com.dtstack.batch.dao.BatchTaskShadeDao;
import com.dtstack.batch.dao.BatchTaskVersionDao;
import com.dtstack.batch.domain.BatchTaskVersionDetail;
import com.dtstack.batch.domain.User;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.web.task.vo.result.BatchTaskShadePageQueryResultVO;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
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
        dto.setAppType(AppType.RDOS.getType());
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
