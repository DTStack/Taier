package com.dtstack.rdos.engine.entrance;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.entrance.log.LogComponent;
import com.dtstack.rdos.engine.entrance.log.LogbackComponent;

/**
 * 
 * Reason: TODO ADD REASON(可选) Date: 2017年02月17日 下午8:57:21 Company:
 * www.dtstack.com
 * 
 * @author sishu.yss
 *
 */
public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private static LogComponent logbackComponent = new LogbackComponent();

	public static void main(String[] args) {
		try {
			CommandLine cmdLine = OptionsProcessor.parseArg(args);
			CmdLineParams.setLine(cmdLine);
			// logger config
			logbackComponent.setupLogger();
			// assembly pipeline
		} catch (Exception e) {
			logger.error("node start error:{}",
					ExceptionUtil.getErrorMessage(e));
			System.exit(-1);
		}
	}
}
