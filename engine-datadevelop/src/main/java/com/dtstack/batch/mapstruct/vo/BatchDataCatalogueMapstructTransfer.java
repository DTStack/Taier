package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchDataCatalogue;
import com.dtstack.batch.web.table.vo.query.BatchDataCatalogueVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchDataCatalogueMapstructTransfer {

    BatchDataCatalogueMapstructTransfer INSTANCE = Mappers.getMapper(BatchDataCatalogueMapstructTransfer.class);


    /**
     * BatchDataCatalogueVO  -> BatchDataCatalogue
     * @param catalogueVO
     * @return
     */
    BatchDataCatalogue BatchDataCatalogueVOToBatchDataCatalogue(BatchDataCatalogueVO catalogueVO);

}

