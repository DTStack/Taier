package com.dtstack.rdos.engine.entrance;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年11月30日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class CmdLineParams {
	
	private static Logger logger  = LoggerFactory.getLogger(CmdLineParams.class);
	
	
	private static CommandLine line;
	
	public static void setLine(CommandLine line) {
		CmdLineParams.line = line;
	}
	
	
	public static String getConfigFilePath(){
		return line.getOptionValue("f");
	}
	

	public static String getLogFilePath(){
		return line.getOptionValue("l");
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
