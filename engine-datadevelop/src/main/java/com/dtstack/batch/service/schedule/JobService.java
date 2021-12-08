package com.dtstack.batch.service.schedule;

import com.alibaba.fastjson.JSON;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.engine.master.action.fill.FillDataRunnable;
import com.dtstack.engine.master.dto.fill.ScheduleFillDataInfoDTO;
import com.dtstack.engine.master.dto.fill.ScheduleFillJobParticipateDTO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.domain.ScheduleFillDataJob;
import com.dtstack.engine.master.pool.FillDataThreadPoolExecutor;
import com.dtstack.engine.pluginapi.util.DateUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


/**
 * @Auther: dazhi
 * @Date: 2021/12/7 3:26 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FillDataJobService fillDataJobService;

    @Autowired
    private FillDataThreadPoolExecutor fillDataThreadPoolExecutor;

    public Long fillData(ScheduleFillJobParticipateDTO scheduleFillJobParticipateDTO) {
        // 必要的校验
        checkFillDataParams(scheduleFillJobParticipateDTO);

        // 生成schedule_fill_data_job数据
        ScheduleFillDataJob fillDataJob = buildScheduleFillDataJob(scheduleFillJobParticipateDTO);
        fillDataJobService.save(fillDataJob);

        ScheduleFillDataInfoDTO fillDataInfo = scheduleFillJobParticipateDTO.getFillDataInfo();
        // 提交任务
        fillDataThreadPoolExecutor.submit(
                new FillDataRunnable(fillDataJob.getId()
                        ,scheduleFillJobParticipateDTO.getFillName()
                        ,fillDataInfo.getFillDataType()
                        ,fillDataInfo.getProjects()
                        ,fillDataInfo.getTaskIds()
                        ,fillDataInfo.getWhitelist()
                        ,fillDataInfo.getBlacklist()
                        ,fillDataInfo.getRootTaskId()
                        ,scheduleFillJobParticipateDTO.getStartDay()
                        ,scheduleFillJobParticipateDTO.getEndDay()
                        ,scheduleFillJobParticipateDTO.getBeginTime()
                        ,scheduleFillJobParticipateDTO.getEndTime()
                        ,scheduleFillJobParticipateDTO.getTenantId()
                        ,scheduleFillJobParticipateDTO.getUserId()
                        ,(fillId,originalStatus,currentStatus)->{
                            ScheduleFillDataJob updateFillDataJob = new ScheduleFillDataJob();
                            updateFillDataJob.setFillGeneratStatus(currentStatus);
                            fillDataJobService.lambdaUpdate()
                                .eq(ScheduleFillDataJob::getId,fillId)
                                .eq(ScheduleFillDataJob::getFillGeneratStatus,originalStatus)
                                .update(updateFillDataJob);
                        }
                        ,applicationContext)
        );
        return fillDataJob.getId();
    }

    /**
     * @author newman
     * @Description 校验补数据任务参数
     * @Date 2020-12-14 17:47
     * @param scheduleFillJobParticipateDTO
     * @return: void
     **/
    private void checkFillDataParams(ScheduleFillJobParticipateDTO scheduleFillJobParticipateDTO) {
        String fillName = scheduleFillJobParticipateDTO.getFillName();
        String startDay = scheduleFillJobParticipateDTO.getStartDay();
        String endDay = scheduleFillJobParticipateDTO.getEndDay();
        ScheduleFillDataInfoDTO fillDataInfo = scheduleFillJobParticipateDTO.getFillDataInfo();
        DateTime startTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(startDay, DateUtil.DATE_FORMAT));
        DateTime endTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(endDay, DateUtil.DATE_FORMAT));

        if (fillName == null) {
            throw new RdosDefineException("(fillName 参数不能为空)", ErrorCode.INVALID_PARAMETERS);
        }

        //补数据的名称中-作为分割名称和后缀信息的分隔符,故不允许使用
        if (fillName.contains("-")) {
            throw new RdosDefineException("(fillName 参数不能包含字符 '-')", ErrorCode.INVALID_PARAMETERS);
        }

        if (!startTime.isBefore(DateTime.now())) {
            throw new RdosDefineException("(补数据业务日期开始时间不能晚于结束时间)", ErrorCode.INVALID_PARAMETERS);
        }

        if (fillDataInfo == null) {
            throw new RdosDefineException("fillDataInfo is not null", ErrorCode.INVALID_PARAMETERS);
        }

        if (FillDataTypeEnum.PROJECT.getType().equals(fillDataInfo.getFillDataType()) &&
                (endTime.getMillis() - startTime.getMillis()) / (1000 * 3600 * 24) > 7) {
            throw new RdosDefineException("The difference between the start and end days cannot exceed 7 days", ErrorCode.INVALID_PARAMETERS);
        }

        //判断补数据的名字每个project必须是唯一的
        boolean existsName = fillDataJobService.checkExistsName(fillName);
        if (existsName) {
            throw new RdosDefineException("补数据任务名称已存在", ErrorCode.NAME_ALREADY_EXIST);
        }
    }

    private ScheduleFillDataJob buildScheduleFillDataJob(ScheduleFillJobParticipateDTO scheduleFillJobParticipateDTO) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();

        scheduleFillDataJob.setFillDataInfo(JSON.toJSONString(scheduleFillJobParticipateDTO.getFillDataInfo()));
        scheduleFillDataJob.setFillGeneratStatus(FillGeneratStatusEnum.REALLY_GENERATED.getType());
        scheduleFillDataJob.setFromDay(scheduleFillJobParticipateDTO.getStartDay());
        scheduleFillDataJob.setToDay(scheduleFillJobParticipateDTO.getEndDay());
        scheduleFillDataJob.setJobName(scheduleFillJobParticipateDTO.getFillName());
        scheduleFillDataJob.setMaxParallelNum(scheduleFillJobParticipateDTO.getMaxParallelNum());
        scheduleFillDataJob.setTenantId(scheduleFillJobParticipateDTO.getTenantId());
        scheduleFillDataJob.setCreateUserId(scheduleFillJobParticipateDTO.getUserId());
        scheduleFillDataJob.setRunDay(DateTime.now().toString(DateUtil.DATE_FORMAT));
        scheduleFillDataJob.setNumberParallelNum(scheduleFillJobParticipateDTO.getMaxParallelNum());
        scheduleFillDataJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleFillDataJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        return scheduleFillDataJob;
    }
}
