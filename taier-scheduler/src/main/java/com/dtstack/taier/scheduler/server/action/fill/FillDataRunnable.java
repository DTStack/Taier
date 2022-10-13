package com.dtstack.taier.scheduler.server.action.fill;

import com.dtstack.taier.scheduler.dto.fill.FillDataChooseTaskDTO;
import com.dtstack.taier.scheduler.dto.fill.FillDataInfoDTO;
import com.dtstack.taier.scheduler.dto.fill.ScheduleFillDataInfoDTO;
import com.dtstack.taier.scheduler.dto.fill.ScheduleFillJobParticipateDTO;
import com.dtstack.taier.scheduler.enums.FillDataTypeEnum;
import com.dtstack.taier.scheduler.enums.FillGeneratStatusEnum;
import com.dtstack.taier.scheduler.server.builder.FillDataJobBuilder;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 1:59 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataRunnable implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(FillDataRunnable.class);

    private Boolean isSuccess = Boolean.TRUE;
    private final String fillName;
    private final Integer fillDataType;
    private final Long fillId;
    private final String startDay;
    private final String endDay;
    private final String beginTime;
    private final String endTime;

    private final List<FillDataChooseTaskDTO> taskIds;
    private final FillDataChooseTaskDTO rootTaskId;
    private final ApplicationContext applicationContext;

    private final FillFinishEvent fillFinishEvent;
    private final FillDataJobBuilder fillDataJobBuilder;

    public FillDataRunnable(Long fillId,
                            ScheduleFillJobParticipateDTO scheduleFillJobParticipateDTO,
                            ScheduleFillDataInfoDTO fillDataInfo,
                            FillFinishEvent fillFinishEvent,
                            ApplicationContext applicationContext) {
        this.fillId = fillId;
        this.fillName = scheduleFillJobParticipateDTO.getFillName();
        this.startDay = scheduleFillJobParticipateDTO.getStartDay();
        this.endDay = scheduleFillJobParticipateDTO.getEndDay();
        this.beginTime = scheduleFillJobParticipateDTO.getBeginTime();
        this.endTime = scheduleFillJobParticipateDTO.getEndTime();
        this.fillDataType = fillDataInfo.getFillDataType();
        this.taskIds = fillDataInfo.getTaskIds();
        this.rootTaskId = fillDataInfo.getRootTaskId();
        this.fillFinishEvent = fillFinishEvent;
        this.applicationContext = applicationContext;

        fillDataJobBuilder = applicationContext.getBean(FillDataJobBuilder.class);
    }

    @Override
    public void run() {
        Integer status;
        try {
            FillDataTask fillDataTask = getFillDataTask();

            if (fillDataTask == null) {
                isSuccess = Boolean.FALSE;
                LOGGER.error("fillId:{} fail fillDataType:{} msg:Supplement data type: 0 Batch supplement data 1 Project supplement data", fillId, fillDataType);
                return;
            }

            // 所有要运行的节点集合 R集合
            LOGGER.info("fillId:{} start getRunList", fillId);
            Set<Long> run = fillDataTask.getRunList();

            // 所有要生成的节点集合 A集合
            Set<Long> all;
            if (rootTaskId == null) {
                // 如果rootTaskId是空，说明没有根节点，需要补充计算出有效路径
                all = fillDataTask.getAllList(run);
            } else {
                // 存在根节点，逻辑是补根节点及其下游，所以不需要计算有效路径
                all = Sets.newHashSet(run);
            }

            // 生成补数据实例
            fillDataJobBuilder.createFillJob(all, run, fillId, fillName, beginTime, endTime, startDay, endDay);

        } catch (Throwable e) {
            LOGGER.error("fillId:{} create exception:", fillId, e);
            isSuccess = Boolean.FALSE;
        }

        if (isSuccess) {
            status = FillGeneratStatusEnum.FILL_FINISH.getType();
        } else {
            status = FillGeneratStatusEnum.FILL_FAIL.getType();
        }

        fillFinishEvent.finishFill(fillId, FillGeneratStatusEnum.REALLY_GENERATED.getType(), status);
    }


    private FillDataTask getFillDataTask() {
        if (FillDataTypeEnum.BATCH.getType().equals(fillDataType)) {
            return new BatchFillDataTask(applicationContext, new FillDataInfoDTO(taskIds, rootTaskId));
        }
        return null;
    }

    public interface FillFinishEvent {
        void finishFill(Long fillId, Integer originalStatus, Integer currentStatus);
    }
}
