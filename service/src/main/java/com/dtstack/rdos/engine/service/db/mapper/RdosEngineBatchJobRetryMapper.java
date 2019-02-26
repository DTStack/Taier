package com.dtstack.rdos.engine.service.db.mapper;

import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface RdosEngineBatchJobRetryMapper {

	void insert(RdosEngineBatchJob rdosEngineBatchJob);
}
