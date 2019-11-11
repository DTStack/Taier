package com.dtstack.engine.common;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.restart.ARestartService;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * FIXME 添加新的方法的时候注意要对{@link ClientProxy}一起修改
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IClient {

    void init(Properties prop) throws Exception;

    JobResult submitJob(JobClient jobClient);

    JobResult cancelJob(JobIdentifier jobIdentifier);

    RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException;

	String getJobMaster(JobIdentifier jobIdentifier);
	
	String getMessageByHttp(String path);

	String getJobLog(JobIdentifier jobIdentifier);

	boolean judgeSlots(JobClient jobClient);

	List<String> getContainerInfos(JobIdentifier jobIdentifier);

	String getCheckpoints(JobIdentifier jobIdentifier);

	ARestartService getRestartService();

}
