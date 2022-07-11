package com.dtstack.taier.develop.service.develop.task;

import com.dtstack.taier.common.enums.EScheduleJobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: zhichen
 * @Date: 2022/05/29/5:35 PM
 */
@Component
public class DevelopTaskTemplateFactory {
    private static final Logger logger = LoggerFactory.getLogger(DevelopTaskTemplateFactory.class);

    @Autowired
    public List<DevelopTaskTemplate> developTaskTemplates;
    public final static HashMap<Integer, DevelopTaskTemplate> TASK_MAP = new HashMap<>();

    @PostConstruct
    private void init() {
        for (DevelopTaskTemplate task : developTaskTemplates) {
            TASK_MAP.put(task.getEScheduleJobType().getType(),task);
        }
        TASK_MAP.put(EScheduleJobType.DATA_ACQUISITION.getType(), TASK_MAP.get(EScheduleJobType.SYNC.getType()));
        logger.info("init DbBuilderFactory success...");
    }

    public DevelopTaskTemplate getTaskImpl(Integer taskType) {
        if (taskType == null) {
            throw new RuntimeException("taskType should not be null !");
        }
        return TASK_MAP.get(taskType);
    }

}
