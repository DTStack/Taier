package com.dtstack.engine.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.mapper.ScheduleTaskShadeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 11:00 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleTaskService extends ServiceImpl<ScheduleTaskShadeMapper, ScheduleTaskShade> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskService.class);


}
