package com.dtstack.engine.master.action.fill;

import com.dtstack.engine.master.dto.fill.FillDataChooseTaskDTO;
import com.dtstack.engine.master.dto.fill.FillDataInfoDTO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/9/13 1:50 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BatchFillDataTask extends AbstractRecursionFillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(BatchFillDataTask.class);

    public BatchFillDataTask(ApplicationContext applicationContext, FillDataInfoDTO fillDataInfoDTO) {
        super(applicationContext, fillDataInfoDTO);
    }

    @Override
    public FillDataTypeEnum setFillDataType(Integer fillDataType) {
        return FillDataTypeEnum.BATCH;
    }

    @Override
    public Set<Long> getRunList() {
        Set<Long> runList = Sets.newHashSet();
        FillDataChooseTaskDTO rootTaskId = this.fillDataInfoBO.getRootTaskId();

//        if (rootTaskId == null) {
//            List<FillDataChooseTaskDTO> taskIds = this.fillDataInfoBO.getTaskChooseList();
//            taskIds.forEach(task -> runList.add(task.getTaskId().toString()));
//        } else {
//            List<String> rootKey = Lists.newArrayList(rootTaskId.getTaskId().toString());
//            runList.addAll(rootKey);
//            int level = 0;
//            List<ScheduleTaskTaskShadeDTO> taskTaskShadeDTOS = scheduleTaskTaskShadeService.listChildByTaskKeys(rootKey);
//            while (CollectionUtils.isNotEmpty(taskTaskShadeDTOS)) {
//                List<String> taskKeys = taskTaskShadeDTOS.stream().map(ScheduleTaskTaskShadeDTO::getTaskKey).collect(Collectors.toList());
//                runList.addAll(taskKeys);
//
//                level++;
//
//                if (level > environmentContext.getFillDataRootTaskMaxLevel()) {
//                    LOGGER.warn("rootTaskId:{} max:{} break cycle",rootTaskId.getTaskId(),level);
//                    break;
//                }
//
//                taskTaskShadeDTOS = scheduleTaskTaskShadeService.listChildByTaskKeys(taskKeys);
//            }
//        }

        return runList;
    }


}
