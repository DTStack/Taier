package com.dtstack.taier.develop.service.schedule;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.dao.mapper.ScheduleJobExpandMapper;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 9:49 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobExpandService extends ServiceImpl<ScheduleJobExpandMapper, ScheduleJobExpand> {

    public ScheduleJobExpand selectOneByJobId(String jobId) {
        // 查询当前日志
        return this.lambdaQuery()
                .eq(ScheduleJobExpand::getIsDeleted, Deleted.NORMAL.getStatus())
                .eq(ScheduleJobExpand::getJobId, jobId)
                .one();
    }

}
