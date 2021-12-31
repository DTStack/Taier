package com.dtstack.engine.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.mapper.ScheduleTaskShadeMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 11:00 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleTaskService extends ServiceImpl<ScheduleTaskShadeMapper, ScheduleTaskShade> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskService.class);

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
