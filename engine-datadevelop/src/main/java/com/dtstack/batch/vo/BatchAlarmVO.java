package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchAlarm;
import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class BatchAlarmVO extends BatchAlarm{
    /**
     * 允许通知的开始时间，如5：00，早上5点
     */
    private String startTime;
    /**
     * 允许通知的结束时间，如22：00，不接受告警
     */
    private String endTime;

    private String webhook;
}
