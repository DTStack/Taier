package com.dtstack.rdos.engine.execution.datax;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
    private static final Logger logger = LoggerFactory.getLogger(SyncClient.class);

	@Override
	public void init(Properties prop) {
		// TODO Auto-generated method stub
		
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
	
	@Override
	public JobResult submitSyncJob(JobClient jobClient){
    	return null;
    }

}
