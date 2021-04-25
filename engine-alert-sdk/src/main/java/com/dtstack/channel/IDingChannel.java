package com.dtstack.channel;

import com.dtstack.lang.data.R;
import com.dtstack.lang.exception.BizException;

import java.util.List;
import java.util.Map;


public interface IDingChannel {

	/**
	 * 钉钉发送
	 * @param hookUrls 钉钉机器人的webhook
	 * @param message 钉钉消息内容
	 * @param extMap 动态参数
	 * @return
	 * @throws BizException
	 */
	public R sendDing(List<String> hookUrls, String message, Map<String, String> dynamicParams, Map<String, Object> extMap) throws BizException;

    /**
     * 以markdown格式发送钉钉消息
     * @param hookUrls  钉钉机器人的webhook
     * @param title 主题
     * @param message 钉钉消息内容
     * @param extMap 动态参数
     * @return
     * @throws BizException
     */
    public R sendDingWithMarkDown(List<String> hookUrls, String title, String message, Map<String, String> dynamicParams, Map<String, Object> extMap) throws BizException;
    
}
