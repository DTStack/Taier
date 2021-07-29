package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchCatalogue;
import com.dtstack.batch.domain.BatchCatalogueVO;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.web.catalogue.vo.query.BatchCatalogueAddVO;
import com.dtstack.batch.web.catalogue.vo.query.BatchCatalogueUpdateVO;
import com.dtstack.batch.web.catalogue.vo.result.BatchCatalogueResultVO;
import com.dtstack.batch.web.catalogue.vo.result.ReadWriteLockVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchCatalogueMapstructTransfer {
    BatchCatalogueMapstructTransfer INSTANCE = Mappers.getMapper(BatchCatalogueMapstructTransfer.class);

    /**
     * IdeCatalogueAddVO -> BatchCatalogue
     *
     * @param vo
     * @return
     */
    BatchCatalogue newCatalogueAddVoToCatalogueVo(BatchCatalogueAddVO vo);

    /**
     * IdeCatalogueUpdateVO ->BatchCatalogueVO
     *
     * @param vo
     * @return
     */
    BatchCatalogueVO newCatalogueUpdateVoToCatalogueVo(BatchCatalogueUpdateVO vo);


    /**
     * com.dtstack.batch.vo.ReadWriteLockVO -> ReadWriteLockVO
     * @param readWriteLockVO
     * @return
     */
    @Mapping(source = "isGetLock", target = "getLock")
    ReadWriteLockVO readWriteLockVOToReadWriteLockVO(com.dtstack.batch.vo.ReadWriteLockVO readWriteLockVO);

    /**
     * CatalogueVO  ->  BatchCatalogueResultVO
     *
     * @param vo
     * @return
     */
    BatchCatalogueResultVO newCatalogueVoToCatalogueResultVo(CatalogueVO vo);
}
