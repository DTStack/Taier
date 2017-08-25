package com.dtstack.rdos.base.flink;

import java.util.Properties;

import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.flink120.FlinkClient;

public class Test {

	public static void main(String[] args) throws Exception{
		    Properties flinkEngine = new Properties();
		    flinkEngine.put("typeName", "flink120");
	        flinkEngine.put("flinkZkAddress", "172.16.1.151");
	        flinkEngine.put("flinkZkNamespace", "/flink");
	        flinkEngine.put("flinkClusterId", "default");
	        flinkEngine.put("jarTmpDir", "D:\\tmp");
		    FlinkClient flinkClient = new FlinkClient();
		    flinkClient.init();
	}
}
