package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.mapper.ScheduleJobJobMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:15 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobJobService extends ServiceImpl<ScheduleJobJobMapper, ScheduleJobJob> {

    /**
     * 查询父实例信息
     *
     * @param jobKeys 实例key
     * @return 实例关系信息
     */
    public List<ScheduleJobJob> listByJobKeys(List<String> jobKeys) {
        if (CollectionUtils.isNotEmpty(jobKeys)) {
            return this.lambdaQuery().in(ScheduleJobJob::getJobKey,jobKeys)
                    .eq(ScheduleJobJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
        }
        return Lists.newArrayList();
    }
}
