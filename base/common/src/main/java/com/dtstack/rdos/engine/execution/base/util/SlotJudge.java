package com.dtstack.rdos.engine.execution.base.util;

import com.dtstack.rdos.engine.execution.base.ClientOperator;
import com.dtstack.rdos.engine.execution.base.ResultMsgDealerUtil;
import com.dtstack.rdos.engine.execution.base.enums.EngineType;

/**
 * FIXME
 * Date: 2017/11/27
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SlotJudge {

    public final static String FLINK_EXCEPTION_URL = "/jobs/%s/exceptions";

    public static boolean judgeSlotsAndAgainExecute(String engineType, String jobId, String pluginInfo) {

        if(EngineType.isFlink(engineType)){
            String message = ClientOperator.getInstance().getEngineMessageByHttp(engineType,
                    String.format(FLINK_EXCEPTION_URL, jobId), pluginInfo);
            return ResultMsgDealerUtil.getInstance().checkNOResource(engineType, message);
        }

        //不对spark资源不足进行判断

        return false;
    }

}
