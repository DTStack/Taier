package com.dtstack.taiga.scheduler.server.action.restart;

import com.dtstack.taiga.common.env.EnvironmentContext;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午7:15
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractRestartJob extends AbstractRestart {

    public AbstractRestartJob(EnvironmentContext environmentContext, ApplicationContext applicationContext) {
        super( environmentContext, applicationContext);
    }

    /**
     * 计算resumeBatch集合
     *
     * @param jobs 重跑实例
     * @return
     */
    public abstract Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs);
}
