package com.dtstack.rdos.engine.db.mapper;

import com.dtstack.rdos.engine.db.dataobject.RdosBatchJob;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosBatchJobMapper {
	
	public void updateJobStatus(@Param("jobId") String jobId, @Param("status") int stauts);
	
	public void updateJobEngineIdAndStatus(@Param("jobId") String jobId,@Param("engineId") String engineId, @Param("status") int stauts);

	public void updateJobEngineId(@Param("jobId") String jobId,@Param("engineId") String engineId);

	public RdosBatchJob getRdosJobByJobId(@Param("jobId")String jobId);

}
