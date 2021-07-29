package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchTableCollection;
import com.dtstack.batch.web.table.vo.query.BatchTableCollectionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchTableCollectMapstructTransfer {

    BatchTableCollectMapstructTransfer INSTANCE = Mappers.getMapper(BatchTableCollectMapstructTransfer.class);

    /**
     * BatchTableCollectionVO -> BatchTableCollection
     * @param vo
     * @return
     */
    BatchTableCollection BatchTableCollectionVOToBatchTableCollection(BatchTableCollectionVO vo);

}
