package com.dtstack.rdos.base.flink;

import com.dtstack.rdos.engine.execution.base.IClient;

public class Test {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		IClient client = (IClient) Class.forName("com.dtstack.rdos.engine.execution.flink120.FlinkClient").newInstance();
		
	}
}
