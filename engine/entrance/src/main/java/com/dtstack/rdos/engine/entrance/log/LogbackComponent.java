package com.dtstack.rdos.engine.entrance.log;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;

import com.dtstack.rdos.engine.entrance.CmdLineParams;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月23日 下午1:27:16
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class LogbackComponent extends LogComponent{
	
	private static String formatePattern ="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [%file:%line] - %msg%n";
	
	private static int day = 7;
	
	@Override
	public void setupLogger() {
	    String file = checkFile();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger newLogger =loggerContext.getLogger("ROOT");
        //Remove all previously added appenders from this logger instance.
        newLogger.detachAndStopAllAppenders();
        //define appender
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();
        //policy
        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<ILoggingEvent>();
        policy.setContext(loggerContext);
        policy.setMaxHistory(day);
        policy.setFileNamePattern(formateLogFile(file));
        policy.setParent(appender);
        policy.start();
        //encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern(formatePattern);
        encoder.start();
        //start appender
        appender.setRollingPolicy(policy);
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        appender.setPrudent(true); //support that multiple JVMs can safely write to the same file.
        appender.start();
        newLogger.addAppender(appender);
        //setup level
        setLevel(newLogger);
        //remove the appenders that inherited 'ROOT'.
        newLogger.setAdditive(false);
	}
	
	private String formateLogFile(String file){
		int index =file.indexOf(".");
		if(index>=0){
			file =file.substring(0, index);
		}
		file =file+"_%d{yyyy-MM-dd}.log";
		return file;
	}

     public void setLevel(Logger logger){
    		if (CmdLineParams.hasOptionTrace()) {
    			logger.setLevel(Level.TRACE);
    		} else if (CmdLineParams.hasOptionDebug()) {
    			logger.setLevel(Level.DEBUG);
    		} else if (CmdLineParams.hasOptionInfo()) {
    			logger.setLevel(Level.INFO);
    		} else if(CmdLineParams.hasOptionWarn()){
    			logger.setLevel(Level.WARN);
    		}else if (CmdLineParams.hasOptionError()){
    			logger.setLevel(Level.ERROR);
    		}else {
    			logger.setLevel(Level.WARN);
    		}
     }
}
