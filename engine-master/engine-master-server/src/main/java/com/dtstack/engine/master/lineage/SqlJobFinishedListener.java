package com.dtstack.engine.master.lineage;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.event.ScheduleJobBatchEvent;
import com.dtstack.engine.master.event.ScheduleJobEventLister;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author chener
 * @Classname SqlJobFinishedListener
 * @Description TODO
 * @Date 2020/12/11 11:29
 * @Created chener@dtstack.com
 */
public abstract class SqlJobFinishedListener implements ScheduleJobEventLister {

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Override
    public void publishBatchEvent(ScheduleJobBatchEvent event) {
        Integer status = event.getStatus();
        if (!RdosTaskStatus.FINISHED.getStatus().equals(status)){
            return;
        }
        List<String> jobIds = event.getJobIds();
        for (String jobId:jobIds){
            ScheduleJob scheduleJob = getScheduleJobByJobId(jobId);
            ScheduleTaskShade taskShade = getScheduleTaskShadeByJobId(jobId);
            if (focusedAppType().getType() != (scheduleJob.getAppType())){
                continue;
            }
            Integer taskType = scheduleJob.getTaskType();
            EScheduleJobType eJobType = EScheduleJobType.getEJobType(taskType);
            if (Objects.isNull(eJobType)){
                throw new RdosDefineException("不支持的任务类型");
            }
            if (!focusedJobTypes().contains(eJobType)){
                continue;
            }
            onFocusedJobFinished(taskShade,scheduleJob,RdosTaskStatus.FINISHED.getStatus());
        }

    }

    /**
     * 当关注任务执行成功调用
     * @param taskShade task
     * @param scheduleJob job
     * @param status status
     */
    protected abstract void onFocusedJobFinished(ScheduleTaskShade taskShade, ScheduleJob scheduleJob, Integer status);

    /**
     * 关注任务类型
     * @return
     */
    public abstract Set<EScheduleJobType> focusedJobTypes();

    /**
     * 关注应用类型
     * @return
     */
    public abstract AppType focusedAppType();



    ScheduleJob getScheduleJobByJobId(String jobId){
        ScheduleJob job = scheduleJobDao.getByJobId(jobId, 0);
        if (Objects.isNull(job)){
            throw new RdosDefineException("jobid:"+jobId+" 实例不存在");
        }
        return job;
    }

    ScheduleTaskShade getScheduleTaskShadeByJobId(String jobId){
        ScheduleJob scheduleJob = getScheduleJobByJobId(jobId);
        Long taskId = scheduleJob.getTaskId();
        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId, focusedAppType().getType());
        if (Objects.isNull(taskShade)){
            throw new RdosDefineException("taskId:"+taskId+" 任务不存在");
        }
        return taskShade;
    }
}
