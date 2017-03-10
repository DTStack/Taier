package com.dtstack.rdos.engine.entrance;

import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.entrance.configs.YamlConfig;
import com.dtstack.rdos.engine.entrance.http.EHttpServer;
import com.dtstack.rdos.engine.entrance.log.LogComponent;
import com.dtstack.rdos.engine.entrance.log.LogbackComponent;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.SubmitContainer;
import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;

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

	private static SubmitContainer submitContainer;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		try {
			CommandLine cmdLine = OptionsProcessor.parseArg(args);
			CmdLineParams.setLine(cmdLine);
			// set logger
			logbackComponent.setupLogger();
			// load config
			Map<String,Object> nodeConfig = new YamlConfig().parse(CmdLineParams.getConfigFilePath(),Map.class);
			// init service
			initService(nodeConfig);
			// add hook
			addShutDownHook();
		} catch (Exception e) {
			//logger.error("node start error:{}",ExceptionUtil.getErrorMessage(e));
			logger.error("node start error:{}", e);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static void initService(Map<String,Object> nodeConfig) throws Exception{
		eHttpServer = new EHttpServer(nodeConfig);
		zkDistributed = ZkDistributed.createZkDistributed(nodeConfig).zkRegistration();
		submitContainer = SubmitContainer.createSubmitContainer(ClientType.Flink, nodeConfig);
	}
	
	private static void addShutDownHook(){
		new ShutDownHook(eHttpServer,zkDistributed,submitContainer).addShutDownHook();
	}
}
