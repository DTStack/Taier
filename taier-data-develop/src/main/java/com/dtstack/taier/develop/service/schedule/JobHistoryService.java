package com.dtstack.taier.develop.service.schedule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleJobHistory;
import com.dtstack.taier.dao.mapper.ScheduleJobHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobHistoryService extends ServiceImpl<ScheduleJobHistoryMapper, ScheduleJobHistory> {

    public String getEngineIdByApplicationId(String applicationId) {
        ScheduleJobHistory scheduleJobHistory = getBaseMapper().selectOne(Wrappers.lambdaQuery(ScheduleJobHistory.class)
                .eq(ScheduleJobHistory::getApplicationId, applicationId));
        return scheduleJobHistory == null ? "" : scheduleJobHistory.getEngineJobId();

    }

    public List<ScheduleJobHistory> listHistory(String jobId, Integer limit) {
        return getBaseMapper().selectList(Wrappers.lambdaQuery(ScheduleJobHistory.class)
                .eq(ScheduleJobHistory::getJobId, jobId).orderBy(true, true, ScheduleJobHistory::getId).last("limit " + limit));
    }
}
