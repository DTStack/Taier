package com.dtstack.taier.datasource.plugin.yarn.core.util;

import com.dtstack.taier.datasource.api.dto.yarn.YarnApplicationStatus;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;

/**
 * yarn status util
 *
 * @author ：wangchuan
 * date：Created in 下午2:56 2022/3/17
 * company: www.dtstack.com
 */
public class StateUtil {

    public static YarnApplicationState convertToYarnApplicationState(YarnApplicationStatus status) {
        AssertUtils.notNull(status, "status can't be null.");
        return YarnApplicationState.valueOf(status.name());
    }

    public static YarnApplicationStatus convertYarnStatusToStatus(YarnApplicationState yarnApplicationState) {
        AssertUtils.notNull(yarnApplicationState, "status can't be null.");
        return YarnApplicationStatus.valueOf(yarnApplicationState.name());
    }
}
