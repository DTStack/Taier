package com.dtstack.rdos.engine.entrance.log;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.dtstack.rdos.engine.entrance.CmdLineParams;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月23日 下午1:27:09
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class Log4jComponent extends LogComponent{
	
    private static String pattern = "%d %p %C %t %m%n";
	

	@Override
	public void setupLogger() {
		/*
		String file =checkFile();
		DailyRollingFileAppender fa = new DailyRollingFileAppender();
		fa.setName("FileLogger");
		fa.setFile(file);
		fa.setLayout(new PatternLayout(pattern));
		setLevel(fa);
		fa.setAppend(true);
		fa.activateOptions();
		Logger.getRootLogger().addAppender(fa);
		*/
	}
	
	public void setLevel(DailyRollingFileAppender fa){
		/*
		if (CmdLineParams.hasOptionTrace()) {
			fa.setThreshold(Level.TRACE);
			Logger.getRootLogger().setLevel(Level.TRACE);
		} else if (CmdLineParams.hasOptionDebug()) {
			fa.setThreshold(Level.DEBUG);
		} else if (CmdLineParams.hasOptionInfo()) {
			fa.setThreshold(Level.INFO);
		} else if(CmdLineParams.hasOptionWarn()){
			fa.setThreshold(Level.WARN);
		}else if(CmdLineParams.hasOptionError()){
			fa.setThreshold(Level.ERROR);
		}else {
			fa.setThreshold(Level.WARN);
		}
		*/
	}


}
