package com.dtstack.taiga.scheduler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    public boolean checkHasBuildJobGraph(Timestamp triggerTime) {
        return false;
    }

    /**
     * 新增jobTrigger
     * @param timestamp 生成的时间搓
     * @param minJobId 今天扫描的最小id
     */
    public void addJobTrigger(Timestamp timestamp, Long minJobId) {
        JobGraphTrigger jobGraphTrigger = new JobGraphTrigger();
        jobGraphTrigger.setTriggerTime(timestamp);
        jobGraphTrigger.setTriggerType(0);
        jobGraphTrigger.setMinJobId(minJobId);
        //新增jobTrigger
        this.save(jobGraphTrigger);
    }

    /**
     *
     *
     * @param left
     * @param right
     * @return
     */
    public String getMinJobIdByTriggerTime(String left, String right) {
        return this.baseMapper.getMinJobIdByTriggerTime(left,right);
    }
}
