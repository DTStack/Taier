package com.dtstack.engine.alert.channel;

import dt.insight.plat.lang.web.R;
import com.dtstack.engine.common.exception.BizException;

import java.util.List;
import java.util.Map;


public interface ISmsChannel {
	
	/**
	 * 短信发送
	 * @param message 短信内容
	 * @param phones 手机号
	 * @param extMap 动态参数
	 * @return 
	 * @throws BizException
	 */
    public R sendSms(String message, List<String> phones, Map<String, Object> extMap) throws BizException;
    
}
