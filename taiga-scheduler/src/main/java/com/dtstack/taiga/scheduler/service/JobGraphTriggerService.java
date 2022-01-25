package com.dtstack.taiga.scheduler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taiga.common.enums.EScheduleType;
import com.dtstack.taiga.dao.domain.JobGraphTrigger;
import com.dtstack.taiga.dao.mapper.JobGraphTriggerMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/1/5 7:02 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobGraphTriggerService extends ServiceImpl<JobGraphTriggerMapper, JobGraphTrigger> {

    /**
     * 判断在triggerTime时间里是否存在JobGraphTrigger
     * @param triggerTime JobGraphTrigger生成时间
     * @return ture 存在，false 不存在
     */
    public boolean checkHasBuildJobGraph(Timestamp triggerTime) {
        return this.baseMapper.getByTriggerTimeAndTriggerType(triggerTime, EScheduleType.NORMAL_SCHEDULE.getType()) != null;

    }

    /**
     * 新增jobTrigger
     * @param timestamp 生成的时间搓
     */
    public void addJobTrigger(Timestamp timestamp) {
        JobGraphTrigger jobGraphTrigger = new JobGraphTrigger();
        jobGraphTrigger.setTriggerTime(timestamp);
        jobGraphTrigger.setTriggerType(0);
        //新增jobTrigger
        this.save(jobGraphTrigger);
    }
}
