package com.dtstack.engine.alert.factory;

import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.param.AlertParam;
import com.dtstack.lang.data.R;

/**
 * Date: 2020/5/19
 * Company: www.dtstack.com
 * 告警基类
 * @author xiaochen
 */
public interface AlertService {

    R send(AlertParam param);

    AlertGateCode alertGateCode();
}
