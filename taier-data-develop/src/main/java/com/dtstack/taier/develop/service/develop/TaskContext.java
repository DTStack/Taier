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
public class TaskContext implements ApplicationContextAware {

    private ConcurrentHashMap<EScheduleJobType, ITaskService> taskContext = new ConcurrentHashMap(EScheduleJobType.values().length);

    public ITaskService get(EScheduleJobType jobType) {
        return taskContext.get(jobType);
    }

    public ITaskService get(Integer taskType) {
        EScheduleJobType jobType = EScheduleJobType.getByTaskType(taskType);
        return taskContext.get(jobType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ITaskService> beansOfType = applicationContext.getBeansOfType(ITaskService.class);
        beansOfType.forEach((t, service) -> {
            List<EScheduleJobType> support = service.support();
            for (EScheduleJobType eScheduleJobType : support) {
                taskContext.put(eScheduleJobType, service);
            }
        });
    }
}
