package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.BatchServerLogVO;
import com.dtstack.batch.web.server.vo.result.BatchServerLogResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchServerLogMapstructTransfer {

    BatchServerLogMapstructTransfer INSTANCE = Mappers.getMapper(BatchServerLogMapstructTransfer.class);

    /**
     * BatchServerLogVO -> BatchServerLogResultVO
     *
     * @param addVO
     * @return
     */
    BatchServerLogResultVO batchServerLogVOToBatchServerLogResultVO(BatchServerLogVO serverLogVO);
}
