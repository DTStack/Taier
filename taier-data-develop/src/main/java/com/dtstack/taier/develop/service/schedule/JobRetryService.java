package com.dtstack.taier.develop.service.schedule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleEngineJobRetry;
import com.dtstack.taier.dao.mapper.ScheduleEngineJobRetryMapper;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 3:22 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobRetryService extends ServiceImpl<ScheduleEngineJobRetryMapper, ScheduleEngineJobRetry> {

    public void removeByJobId(String jobId) {
        getBaseMapper().delete(Wrappers.lambdaQuery(ScheduleEngineJobRetry.class).eq(
                ScheduleEngineJobRetry::getJobId, jobId));
    }
}
