package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleTaskTaskShade;
import com.dtstack.taier.dao.mapper.ScheduleTaskTaskShadeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 11:01 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleTaskTaskService extends ServiceImpl<ScheduleTaskTaskShadeMapper, ScheduleTaskTaskShade> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskTaskService.class);
}
