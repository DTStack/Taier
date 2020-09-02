package com.dtstack.engine.master.vo;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class ScheduleTaskVO extends com.dtstack.engine.api.vo.ScheduleTaskVO {

    public ScheduleTaskVO() {
    }

    public ScheduleTaskVO(ScheduleTaskShade taskShade, boolean getSimpleParams) {
        BeanUtils.copyProperties(taskShade, this);
        //需要将task复制给id
        this.setId(taskShade.getTaskId());
        init();
        if (getSimpleParams) {
            //精简不需要的参数（尤其是长字符串）
            setTaskDesc(StringUtils.EMPTY);
            setTaskParams(StringUtils.EMPTY);
            setExeArgs(StringUtils.EMPTY);
            setMainClass(StringUtils.EMPTY);
            setScheduleConf(StringUtils.EMPTY);
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
}
