package com.dtstack.engine.master.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.engine.common.enums.Deleted;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.mapper.ScheduleTaskShadeMapper;
import com.dtstack.engine.master.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.pager.PageResult;
import com.dtstack.engine.pluginapi.util.MathUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class ScheduleTaskShadeService extends ServiceImpl<ScheduleTaskShadeMapper, ScheduleTaskShade> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskShadeService.class);

    public List<ScheduleTaskShade> listRunnableTask(Long startId, List<Integer> scheduleStatusList, Integer taskSize) {
        if (startId == null) {
            return Lists.newArrayList();
        }

        if (startId < 0) {
            startId = 0L;
        }
        return this.baseMapper.listRunnableTask(startId,scheduleStatusList,taskSize);
    }


    /**
     * web 接口
     * 例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息
     */
    public void addOrUpdate(ScheduleTaskShadeDTO batchTaskShadeDTO) {

        //保存batch_task_shade
        if (getByTaskId(batchTaskShadeDTO.getTaskId()) != null) {
            //更新提交时间
            batchTaskShadeDTO.setGmtModified(new Timestamp(System.currentTimeMillis()));
            baseMapper.update(batchTaskShadeDTO,Wrappers.lambdaQuery(ScheduleTaskShade.class)
                    .eq(ScheduleTaskShade::getTaskId,batchTaskShadeDTO.getTaskId()));
        } else {
            if (null == batchTaskShadeDTO.getFlowId()) {
                batchTaskShadeDTO.setFlowId(0L);
            }
            if (StringUtils.isNotBlank(batchTaskShadeDTO.getComponentVersion())) {
                batchTaskShadeDTO.setComponentVersion(batchTaskShadeDTO.getComponentVersion());
            }
            baseMapper.insert(batchTaskShadeDTO);
        }
    }




    /**
     * 根据任务id获取对应的taskShade
     * @param taskIds
     * @return
     */
    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return new ArrayList<>();
        }
        return getBaseMapper().selectList(Wrappers.lambdaQuery(ScheduleTaskShade.class)
                .in(ScheduleTaskShade::getTaskId,taskIds));
    }



    public ScheduleTaskShade getByName(String name) {
       return getBaseMapper().selectOne(Wrappers.lambdaQuery(ScheduleTaskShade.class)
               .eq(ScheduleTaskShade::getName,name));
    }



    /**
     * jobKey 格式：cronTrigger_taskId_time
     *
     * @param jobKey
     * @return
     */
    public String getTaskNameByJobKey(String jobKey) {
        String[] jobKeySplit = jobKey.split("_");
        if (jobKeySplit.length < 3) {
            return "";
        }

        String taskIdStr = jobKeySplit[jobKeySplit.length - 2];
        Long taskShadeId = MathUtil.getLongVal(taskIdStr);
        ScheduleTaskShade taskShade = getById(taskShadeId);
        if (taskShade == null) {
            return "";
        }

        return taskShade.getName();
    }


    /**
     * 获取工作流中的最顶层的子节点
     *
     * @param taskId
     * @return
     */
    public ScheduleTaskShade getWorkFlowTopNode(Long taskId,Integer appType) {
        if (taskId != null) {
//            return scheduleTaskShadeDao.getWorkFlowTopNode(taskId,appType);
            return null;
        } else {
            return null;
        }
    }

    /**
     * 分页查询已提交的任务
     */
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(ScheduleTaskShadeDTO dto) {
       /* PageQuery<ScheduleTaskShadeDTO> query = new PageQuery<>(dto.getPageIndex(),dto.getPageSize(),"gmt_modified",dto.getSort());
        query.setModel(dto);
        Integer count = scheduleTaskShadeDao.simpleCount(dto);
        List<ScheduleTaskShadeVO> data = new ArrayList<>();
        if (count > 0) {
            List<ScheduleTaskShade> taskShades = scheduleTaskShadeDao.simpleQuery(query);
            for (ScheduleTaskShade taskShade : taskShades) {
                ScheduleTaskShadeVO taskShadeVO = new ScheduleTaskShadeVO();
                BeanUtils.copyProperties(taskShade,taskShadeVO);
//                taskShadeVO.setId(taskShade.getTaskId());
                taskShadeVO.setTaskName(taskShade.getName());
//                taskShadeVO.setTaskType(taskShade.getTaskType());
//                taskShadeVO.setGmtModified(taskShade.getGmtModified());
//                taskShadeVO.setIsDeleted(taskShade.getIsDeleted());
                data.add(taskShadeVO);
            }
        }
        return new PageResult<>(data, count, query);*/
        return null;
    }


    public ScheduleTaskShade getByTaskId(Long taskId) {
        ScheduleTaskShade taskShade = getBaseMapper().selectOne(
                Wrappers.lambdaQuery(ScheduleTaskShade.class).eq(ScheduleTaskShade::getTaskId,taskId)
                .eq(ScheduleTaskShade::getIsDeleted,Deleted.NORMAL.getStatus()));
        if (taskShade == null) {
            return null;
        }
        return taskShade;
    }


    /**
     *
     * 保存任务提交engine的额外信息
     * @param taskId
     * @param appType
     * @param info
     * @return
     */
    public void info( Long taskId, Integer appType,String info) {

    }



    public ScheduleTaskShade getById(Long id ){
        return getBaseMapper().selectById(id);
    }



    /**
     * 按照appType和taskId分组查询
     * @param groupByAppMap 分组数据
     * @return
     */
    public Map<Integer,List<ScheduleTaskShade>> listTaskShadeByIdAndType(Map<Integer, Set<Long>> groupByAppMap){
        return null;
    }

}
