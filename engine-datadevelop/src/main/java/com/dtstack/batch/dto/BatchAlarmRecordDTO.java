package com.dtstack.batch.dto;

import com.dtstack.batch.domain.BatchAlarmRecord;
import lombok.Data;

import java.util.List;

/**
 * Reason:
 * Date: 2018/1/2
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class BatchAlarmRecordDTO extends BatchAlarmRecord {

    private List<Long> taskIdList;

    private Long startTime;

    private Long endTime;

    private List<Long> alarmIdList;
}
