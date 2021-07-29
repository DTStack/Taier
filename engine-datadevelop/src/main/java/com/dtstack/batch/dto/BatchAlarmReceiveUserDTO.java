package com.dtstack.batch.dto;

import com.dtstack.batch.domain.BatchAlarmReceiveUser;
import lombok.Data;

import java.util.Collection;

/**
 * Reason:
 * Date: 2018/1/3
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class BatchAlarmReceiveUserDTO extends BatchAlarmReceiveUser {

    private Collection<Long> alarmIds;

}
