package com.dtstack.rdos.engine.execution.sync;

import java.io.IOException;
import java.util.Properties;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;

/**
 * 
 * @author sishu.yss
 *
 */
public class SyncClient extends AbsClient{

	@Override
	public void init(Properties prop) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JobResult submitJobWithJar(JobClient jobClient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobResult submitSqlJob(JobClient jobClient) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobResult cancelJob(String jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RdosTaskStatus getJobStatus(String jobId) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJobDetail(String jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobResult immediatelySubmitJob(JobClient jobClient) {
		// TODO Auto-generated method stub
		return null;
	}

}
