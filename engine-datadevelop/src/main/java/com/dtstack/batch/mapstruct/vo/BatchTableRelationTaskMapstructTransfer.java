package com.dtstack.batch.mapstruct.vo;

import com.dtstack.engine.api.domain.BatchTask;
import com.dtstack.batch.web.table.vo.query.BatchTableRelationTaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchTableRelationTaskMapstructTransfer {

    BatchTableRelationTaskMapstructTransfer INSTANCE = Mappers.getMapper(BatchTableRelationTaskMapstructTransfer.class);

    /**
     * BatchTableRelationTaskVO -> BatchTask
     * @param vo
     * @return
     */
    BatchTask BatchTableRelationTaskVOTOBatchTask(BatchTableRelationTaskVO vo);
}
