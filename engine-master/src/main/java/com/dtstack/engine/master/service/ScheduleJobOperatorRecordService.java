package com.dtstack.engine.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.engine.domain.ScheduleJobOperatorRecord;
import com.dtstack.engine.mapper.ScheduleJobOperatorRecordMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:47 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobOperatorRecordService extends ServiceImpl<ScheduleJobOperatorRecordMapper, ScheduleJobOperatorRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobOperatorRecordService.class);

    public List<ScheduleJobOperatorRecord> listStopJob(Long id) {
        if (id != null && id > 0) {
            return this.baseMapper.listStopJob(id);
        }
        return Lists.newArrayList();
    }

    public Integer updateOperatorExpiredVersion(Long id, Timestamp operatorExpired, Integer version) {
        if (id != null && id > 0 && version != null) {
            return this.baseMapper.updateOperatorExpiredVersion(id,operatorExpired,version);
        }
        return 0;
    }
}
