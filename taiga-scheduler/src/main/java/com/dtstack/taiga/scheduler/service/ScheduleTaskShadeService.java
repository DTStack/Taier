package com.dtstack.taiga.scheduler.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taiga.common.enums.Deleted;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.dao.dto.ScheduleTaskShadeDTO;
import com.dtstack.taiga.dao.mapper.ScheduleTaskShadeMapper;
import com.dtstack.taiga.dao.pager.PageResult;
import com.dtstack.taiga.pluginapi.util.MathUtil;
import com.dtstack.taiga.scheduler.vo.ScheduleTaskShadeVO;
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

    /**
     * 生成周期实例，扫描全部任务
     * @param startId 开始位置的taskId
     * @param scheduleStatusList 任务的状态
     * @param taskSize 查询出来最大的任务数
     * @return 任务集合
     */
    public List<ScheduleTaskShade> listRunnableTask(Long startId, List<Integer> scheduleStatusList, Integer taskSize) {
        if (startId == null) {
            return Lists.newArrayList();
        }

        if (startId < 0) {
            startId = 0L;
        }
        return this.baseMapper.listRunnableTask(startId,scheduleStatusList,taskSize);
    }
}
