package com.dtstack.rdos.engine.entrance.log;

import org.apache.commons.lang3.StringUtils;

import com.dtstack.rdos.engine.entrance.CmdLineParams;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月23日 下午1:27:21
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public abstract class LogComponent {
	
	public void setupLogger() {}
	
	protected String checkFile(){
		String logfile = CmdLineParams.getLogFilePath();
		if(StringUtils.isBlank(logfile)){
			return String.format("%s/%s", System.getProperty("user.dir"),"logs/node.log");
		}
		return logfile;
	}
}
