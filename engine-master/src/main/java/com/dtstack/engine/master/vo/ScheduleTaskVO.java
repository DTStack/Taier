package com.dtstack.engine.master.vo;

import com.dtstack.engine.api.domain.ScheduleTask;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.master.parser.ESchedulePeriodType;
import com.dtstack.engine.master.parser.ScheduleCron;
import com.dtstack.engine.master.parser.ScheduleFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class ScheduleTaskVO extends com.dtstack.engine.api.vo.ScheduleTaskVO {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTaskVO.class);
    private static final String EMPYT = "";

    public ScheduleTaskVO() {
    }

    public ScheduleTaskVO(ScheduleTaskShade task) {
        this.setComputeType(task.getComputeType());
        this.setCreateUserId(task.getCreateUserId());
        this.setOwnerUserId(task.getOwnerUserId());
        this.setModifyUserId(task.getModifyUserId());
        this.setEngineType(task.getEngineType());
        this.setName(task.getName());
        this.setNodePid(task.getNodePid());
        this.setScheduleConf(task.getScheduleConf());
        this.setScheduleStatus(task.getScheduleStatus());
        this.setSqlText(task.getSqlText());
        this.setTaskParams(task.getTaskParams());
        this.setTaskType(task.getTaskType());
        this.setVersionId(task.getVersionId());
        this.setGmtCreate(task.getGmtCreate());
        this.setGmtModified(task.getGmtModified());
        this.setTaskId(task.getTaskId());
        this.setIsDeleted(task.getIsDeleted());
        this.setProjectId(task.getProjectId());
        this.setTenantId(task.getTenantId());
        this.setTaskDesc(task.getTaskDesc());
        this.setMainClass(task.getMainClass());
        this.setExeArgs(task.getExeArgs());
        this.setSubmitStatus(task.getSubmitStatus());
        this.setFlowId(task.getFlowId());

        init();
    }

    public ScheduleTaskVO(ScheduleTaskShade taskShade, boolean getSimpleParams) {
        BeanUtils.copyProperties(taskShade, this);
        //需要将task复制给id
        this.setId(taskShade.getTaskId());
        init();
        if (getSimpleParams) {
            //精简不需要的参数（尤其是长字符串）
            setTaskDesc(EMPYT);
            setTaskParams(EMPYT);
            setExeArgs(EMPYT);
            setMainClass(EMPYT);
            setScheduleConf(EMPYT);
        }
    }

    public ScheduleTaskVO(ScheduleTaskForFillDataDTO task) {
        BeanUtils.copyProperties(task, this);
        init();
    }

    private void init() {

        if (StringUtils.isNotBlank(this.getScheduleConf())) {
            try {
                ScheduleCron cron = ScheduleFactory.parseFromJson(this.getScheduleConf());
                this.cron = cron.getCronStr();
                this.taskPeriodId = cron.getPeriodType();
                if (ESchedulePeriodType.MIN.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "分钟任务";
                } else if (ESchedulePeriodType.HOUR.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "小时任务";
                } else if (ESchedulePeriodType.DAY.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "天任务";
                } else if (ESchedulePeriodType.WEEK.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "周任务";
                } else if (ESchedulePeriodType.MONTH.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "月任务";
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void parsePeriodType(){
        if (StringUtils.isNotBlank(this.getScheduleConf())) {
            try{
                ScheduleCron cron = ScheduleFactory.parseFromJson(this.getScheduleConf());
                this.setPeriodType(cron.getPeriodType());
            }catch (Exception e){
                LOG.error("", e);
            }
        }
    }

    public ScheduleTaskVO toVO(ScheduleTask scheduleTask) {
        ScheduleTaskVO batchTaskVO = new ScheduleTaskVO();
        try {
            BeanUtils.copyProperties(scheduleTask, batchTaskVO);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        batchTaskVO.setTaskId(scheduleTask.getId());
        return batchTaskVO;
    }
    public ScheduleTaskVO toVO(ScheduleTask scheduleTask, ScheduleTaskVO batchTaskVO) {
        try {
            BeanUtils.copyProperties(scheduleTask, batchTaskVO);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        batchTaskVO.setTaskId(scheduleTask.getId());
        return batchTaskVO;
    }
}
