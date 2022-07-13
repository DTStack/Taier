package com.dtstack.taier.develop.service.develop;

import com.dtstack.taier.common.enums.EScheduleJobType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskConfiguration implements ApplicationContextAware {

    private ConcurrentHashMap<EScheduleJobType, ITaskRunner> taskRunners = new ConcurrentHashMap(EScheduleJobType.values().length);
    private ConcurrentHashMap<EScheduleJobType, ITaskSaver> taskSavers = new ConcurrentHashMap(EScheduleJobType.values().length);

    public ITaskRunner get(EScheduleJobType jobType) {
        return taskRunners.get(jobType);
    }

    public ITaskRunner get(Integer taskType) {
        EScheduleJobType jobType = EScheduleJobType.getByTaskType(taskType);
        return taskRunners.get(jobType);
    }

    public ITaskSaver getSave(Integer taskType) {
        EScheduleJobType jobType = EScheduleJobType.getByTaskType(taskType);
        return taskSavers.get(jobType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ITaskRunner> beansOfRunner = applicationContext.getBeansOfType(ITaskRunner.class);
        beansOfRunner.forEach((t, service) -> {
            List<EScheduleJobType> support = service.support();
            for (EScheduleJobType eScheduleJobType : support) {
                taskRunners.put(eScheduleJobType, service);
            }
        });

        Map<String, ITaskSaver> beansOfSaver = applicationContext.getBeansOfType(ITaskSaver.class);
        beansOfSaver.forEach((t, service) -> {
            List<EScheduleJobType> support = service.support();
            for (EScheduleJobType eScheduleJobType : support) {
                taskSavers.put(eScheduleJobType, service);
            }
        });

    }
}
