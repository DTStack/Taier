package com.dtstack.rdos.engine.entrance.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年11月30日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class CmdLineParams {
	
	private static CommandLine line;
	
	private static String  dir = System.getProperty("user.dir");
	
	public static void setLine(CommandLine line) {
		CmdLineParams.line = line;
	}
	
	public static String getConfigFilePath(){
		String configFile = line.getOptionValue("f");
		if(StringUtils.isBlank(configFile)){
			return String.format("%s/%s",dir,"config/node.yml");
		}
		return line.getOptionValue("f");
	}
	
	public static String getLogFilePath(){
		String logFile = line.getOptionValue("l");
		if(StringUtils.isBlank(logFile)){
			return String.format("%s/%s",dir,"logs/node.log");
		}
		return logFile;
	}
	
	public static boolean hasOptionTrace(){
		return line.hasOption("vvvvv");
	}
	
	public static boolean hasOptionDebug(){
		return line.hasOption("vvvv");
	}
	
	public static boolean hasOptionInfo(){
		return line.hasOption("vvv");
	}
	

	public static boolean hasOptionWarn(){
		return line.hasOption("vv");
	}
	
	public static boolean hasOptionError(){
		return line.hasOption("v");
	}
}
