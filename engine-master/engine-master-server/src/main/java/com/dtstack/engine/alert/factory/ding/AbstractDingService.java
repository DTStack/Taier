package com.dtstack.engine.alert.factory.ding;

import com.dtstack.engine.alert.enums.DingTypeEnums;
import com.dtstack.engine.alert.factory.AlertService;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.lang.data.R;
import com.dtstack.lang.exception.BizException;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Date: 2020/6/11
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public abstract class AbstractDingService implements AlertService {
    private static String DING_MSG_TYPE_KEY = "msgtype";

    @Override
    public R send(AlertParam param) {
        Map<String, Object> extCfg = param.getExtCfg() == null ? Maps.newHashMap() : param.getExtCfg();
        if (!extCfg.containsKey(DING_MSG_TYPE_KEY)) {
            return sendDing(param);
        }

        DingTypeEnums dingMsgType = DingTypeEnums.getDingTypeEnum((String) extCfg.get(DING_MSG_TYPE_KEY));
        switch (dingMsgType) {
            case TEXT:
                return sendDing(param);
            case MARKDOWN:
                return sendDingWithMarkDown(param);
            default:
                throw new BizException(String.format("不支持的钉钉消息类型，msgtype=%s", dingMsgType));
        }
    }


    public R sendDing(AlertParam param) {
        return null;
    }


    public R sendDingWithMarkDown(AlertParam param) {
        return sendDing(param);
    }


}
