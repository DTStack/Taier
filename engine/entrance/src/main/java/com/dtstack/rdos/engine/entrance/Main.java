package com.dtstack.rdos.engine.entrance;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.entrance.configs.NodeConfig;
import com.dtstack.rdos.engine.entrance.configs.YamlConfig;
import com.dtstack.rdos.engine.entrance.http.EHttpServer;
import com.dtstack.rdos.engine.entrance.log.LogComponent;
import com.dtstack.rdos.engine.entrance.log.LogbackComponent;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;

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
	
	private static EHttpServer eHttpServer;
	
	private static ZkDistributed zkDistributed;

	public static void main(String[] args) {
		try {
			CommandLine cmdLine = OptionsProcessor.parseArg(args);
			CmdLineParams.setLine(cmdLine);
			// set logger
			logbackComponent.setupLogger();
			// load config
			NodeConfig nodeConfig = new YamlConfig().parse(CmdLineParams.getConfigFilePath(),NodeConfig.class);
			// init service
			initService(nodeConfig);
			// add hook
			addShutDownHook();
		} catch (Exception e) {
			logger.error("node start error:{}",ExceptionUtil.getErrorMessage(e));
			System.exit(-1);
		}
	}
	
	private static void initService(NodeConfig nodeConfig) throws Exception{
		eHttpServer = new EHttpServer(nodeConfig.getLocalAddress());
		zkDistributed = ZkDistributed.createZkDistributed(nodeConfig);
	}
	
	private static void addShutDownHook(){
		new ShutDownHook(eHttpServer,zkDistributed).addShutDownHook();
	}
}
