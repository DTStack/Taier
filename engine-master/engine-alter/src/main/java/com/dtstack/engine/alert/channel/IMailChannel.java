package com.dtstack.engine.alert.channel;

import dt.insight.plat.lang.web.R;
import com.dtstack.engine.common.exception.BizException;

import java.io.File;
import java.util.List;
import java.util.Map;


public interface IMailChannel {
	
	/**
	 * 邮箱发送
	 * @param recipients 邮箱
	 * @param subject 主题
	 * @param message 内容
	 * @param attachFiles 附件（可为空）
	 * @param extMap 动态参数
	 * @return 
	 * @throws BizException
	 */
    public R sendMail(List<String> recipients, String subject, String message, List<File> attachFiles, Map<String, Object> extMap) throws BizException;
    
}
