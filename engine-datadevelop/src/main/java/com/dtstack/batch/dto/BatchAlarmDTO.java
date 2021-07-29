package com.dtstack.batch.dto;

import com.dtstack.batch.domain.BatchAlarm;
import lombok.Data;

/**
 * Reason:
 * Date: 2018/1/3
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class BatchAlarmDTO extends BatchAlarm {

    private String taskNameLike;

}
