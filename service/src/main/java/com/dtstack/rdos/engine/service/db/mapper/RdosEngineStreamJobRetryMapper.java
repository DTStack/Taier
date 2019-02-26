package com.dtstack.rdos.engine.service.db.mapper;

import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface RdosEngineStreamJobRetryMapper {

    void insert(RdosEngineStreamJob rdosEngineStreamJob);
}
