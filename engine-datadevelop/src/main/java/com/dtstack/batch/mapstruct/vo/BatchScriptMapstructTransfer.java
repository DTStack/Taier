package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.BatchScriptVO;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.web.catalogue.vo.result.BatchCatalogueResultVO;
import com.dtstack.batch.web.script.vo.query.BatchScriptAddOrUpdateVO;
import com.dtstack.batch.web.script.vo.result.BatchExecuteResultVO;
import com.dtstack.batch.web.script.vo.result.BatchScriptResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchScriptMapstructTransfer {
    BatchScriptMapstructTransfer INSTANCE = Mappers.getMapper(BatchScriptMapstructTransfer.class);

    /**
     * BatchScriptAddOrUpdateVO -> BatchScriptVO
     *
     * @param addVO
     * @return
     */
    BatchScriptVO scriptAddOrUpdateVOToScriptVO(BatchScriptAddOrUpdateVO addVO);

    /**
     * BatchScriptVO -> BatchScriptResultVO
     *
     * @param scriptById
     * @return
     */
    BatchScriptResultVO scriptByIdToBatchScriptResultVO(BatchScriptVO scriptById);

    /**
     * CatalogueVO -> BatchCatalogueResultVO
     *
     * @param catalogueVO
     * @return
     */
    BatchCatalogueResultVO catalogueVOToBatchCatalogueResultVO(CatalogueVO catalogueVO);

    /**
     * ExecuteResultVO -> BatchExecuteResultVO
     *
     * @param executeResultVO
     * @return
     */
    BatchExecuteResultVO executeResultVOToBatchExecuteResultVO(ExecuteResultVO executeResultVO);
}
