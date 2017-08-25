package org.base.flink130;

import java.util.Properties;

import com.dtstack.rdos.engine.execution.flink130.FlinkClient;

public class AppTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		FlinkClient flinkClient = new FlinkClient();
		Properties properties = new Properties();
		properties.put("typeName", "flink");
		properties.put("flinkZkAddress","172.16.10.135:2181,172.16.10.136:2181,172.16.10.138:2181");
		properties.put("flinkZkNamespace","/flink");
		properties.put("flinkClusterId","/default");
		properties.put("flinkHighAvailabilityStorageDir","hdfs://172.16.10.135:9000/flink13/ha");
		properties.put("jarTmpDir", "../tmp");
		flinkClient.init();
	}

}
